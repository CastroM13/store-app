import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IWishlist } from '../wishlist.model';
import { WishlistService } from '../service/wishlist.service';

@Component({
  templateUrl: './wishlist-delete-dialog.component.html',
})
export class WishlistDeleteDialogComponent {
  wishlist?: IWishlist;

  constructor(protected wishlistService: WishlistService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.wishlistService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
