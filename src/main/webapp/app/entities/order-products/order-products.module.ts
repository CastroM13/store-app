import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrderProductsComponent } from './list/order-products.component';
import { OrderProductsDetailComponent } from './detail/order-products-detail.component';
import { OrderProductsUpdateComponent } from './update/order-products-update.component';
import { OrderProductsDeleteDialogComponent } from './delete/order-products-delete-dialog.component';
import { OrderProductsRoutingModule } from './route/order-products-routing.module';

@NgModule({
  imports: [SharedModule, OrderProductsRoutingModule],
  declarations: [OrderProductsComponent, OrderProductsDetailComponent, OrderProductsUpdateComponent, OrderProductsDeleteDialogComponent],
  entryComponents: [OrderProductsDeleteDialogComponent],
})
export class OrderProductsModule {}
