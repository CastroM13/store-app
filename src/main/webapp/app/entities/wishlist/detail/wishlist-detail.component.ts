import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IWishlist } from '../wishlist.model';

@Component({
  selector: 'jhi-wishlist-detail',
  templateUrl: './wishlist-detail.component.html',
})
export class WishlistDetailComponent implements OnInit {
  wishlist: IWishlist | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ wishlist }) => {
      this.wishlist = wishlist;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
