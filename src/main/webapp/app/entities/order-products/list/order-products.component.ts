import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrderProducts } from '../order-products.model';
import { OrderProductsService } from '../service/order-products.service';
import { OrderProductsDeleteDialogComponent } from '../delete/order-products-delete-dialog.component';

@Component({
  selector: 'jhi-order-products',
  templateUrl: './order-products.component.html',
})
export class OrderProductsComponent implements OnInit {
  orderProducts?: IOrderProducts[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected orderProductsService: OrderProductsService,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.orderProductsService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IOrderProducts[]>) => {
            this.isLoading = false;
            this.orderProducts = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.orderProductsService.query().subscribe(
      (res: HttpResponse<IOrderProducts[]>) => {
        this.isLoading = false;
        this.orderProducts = res.body ?? [];
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

  trackId(index: number, item: IOrderProducts): number {
    return item.id!;
  }

  delete(orderProducts: IOrderProducts): void {
    const modalRef = this.modalService.open(OrderProductsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.orderProducts = orderProducts;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
