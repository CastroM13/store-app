import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrderProductsComponent } from '../list/order-products.component';
import { OrderProductsDetailComponent } from '../detail/order-products-detail.component';
import { OrderProductsUpdateComponent } from '../update/order-products-update.component';
import { OrderProductsRoutingResolveService } from './order-products-routing-resolve.service';

const orderProductsRoute: Routes = [
  {
    path: '',
    component: OrderProductsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrderProductsDetailComponent,
    resolve: {
      orderProducts: OrderProductsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrderProductsUpdateComponent,
    resolve: {
      orderProducts: OrderProductsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrderProductsUpdateComponent,
    resolve: {
      orderProducts: OrderProductsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(orderProductsRoute)],
  exports: [RouterModule],
})
export class OrderProductsRoutingModule {}
