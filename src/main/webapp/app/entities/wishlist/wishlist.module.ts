import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WishlistComponent } from './list/wishlist.component';
import { WishlistDetailComponent } from './detail/wishlist-detail.component';
import { WishlistUpdateComponent } from './update/wishlist-update.component';
import { WishlistDeleteDialogComponent } from './delete/wishlist-delete-dialog.component';
import { WishlistRoutingModule } from './route/wishlist-routing.module';

@NgModule({
  imports: [SharedModule, WishlistRoutingModule],
  declarations: [WishlistComponent, WishlistDetailComponent, WishlistUpdateComponent, WishlistDeleteDialogComponent],
  entryComponents: [WishlistDeleteDialogComponent],
})
export class WishlistModule {}
