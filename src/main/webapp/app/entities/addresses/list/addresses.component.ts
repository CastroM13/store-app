import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAddresses } from '../addresses.model';
import { AddressesService } from '../service/addresses.service';
import { AddressesDeleteDialogComponent } from '../delete/addresses-delete-dialog.component';

@Component({
  selector: 'jhi-addresses',
  templateUrl: './addresses.component.html',
})
export class AddressesComponent implements OnInit {
  addresses?: IAddresses[];
  isLoading = false;
  currentSearch: string;

  constructor(protected addressesService: AddressesService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.addressesService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IAddresses[]>) => {
            this.isLoading = false;
            this.addresses = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.addressesService.query().subscribe(
      (res: HttpResponse<IAddresses[]>) => {
        this.isLoading = false;
        this.addresses = res.body ?? [];
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

  trackId(index: number, item: IAddresses): number {
    return item.id!;
  }

  delete(addresses: IAddresses): void {
    const modalRef = this.modalService.open(AddressesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.addresses = addresses;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
