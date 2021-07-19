import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrders } from '../orders.model';
import { OrdersService } from '../service/orders.service';
import { OrdersDeleteDialogComponent } from '../delete/orders-delete-dialog.component';

@Component({
  selector: 'jhi-orders',
  templateUrl: './orders.component.html',
})
export class OrdersComponent implements OnInit {
  orders?: IOrders[];
  isLoading = false;
  currentSearch: string;

  constructor(protected ordersService: OrdersService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.ordersService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IOrders[]>) => {
            this.isLoading = false;
            this.orders = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.ordersService.query().subscribe(
      (res: HttpResponse<IOrders[]>) => {
        this.isLoading = false;
        this.orders = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IOrders): number {
    return item.id!;
  }

  delete(orders: IOrders): void {
    const modalRef = this.modalService.open(OrdersDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.orders = orders;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
