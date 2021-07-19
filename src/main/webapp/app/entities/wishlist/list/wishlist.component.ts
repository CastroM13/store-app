import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IWishlist } from '../wishlist.model';
import { WishlistService } from '../service/wishlist.service';
import { WishlistDeleteDialogComponent } from '../delete/wishlist-delete-dialog.component';

@Component({
  selector: 'jhi-wishlist',
  templateUrl: './wishlist.component.html',
})
export class WishlistComponent implements OnInit {
  wishlists?: IWishlist[];
  isLoading = false;
  currentSearch: string;

  constructor(protected wishlistService: WishlistService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.wishlistService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IWishlist[]>) => {
            this.isLoading = false;
            this.wishlists = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.wishlistService.query().subscribe(
      (res: HttpResponse<IWishlist[]>) => {
        this.isLoading = false;
        this.wishlists = res.body ?? [];
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

  trackId(index: number, item: IWishlist): number {
    return item.id!;
  }

  delete(wishlist: IWishlist): void {
    const modalRef = this.modalService.open(WishlistDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.wishlist = wishlist;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
