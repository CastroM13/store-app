import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { WishlistComponent } from '../list/wishlist.component';
import { WishlistDetailComponent } from '../detail/wishlist-detail.component';
import { WishlistUpdateComponent } from '../update/wishlist-update.component';
import { WishlistRoutingResolveService } from './wishlist-routing-resolve.service';

const wishlistRoute: Routes = [
  {
    path: '',
    component: WishlistComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: WishlistDetailComponent,
    resolve: {
      wishlist: WishlistRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: WishlistUpdateComponent,
    resolve: {
      wishlist: WishlistRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: WishlistUpdateComponent,
    resolve: {
      wishlist: WishlistRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(wishlistRoute)],
  exports: [RouterModule],
})
export class WishlistRoutingModule {}
