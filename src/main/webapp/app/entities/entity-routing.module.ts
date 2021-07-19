import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'users',
        data: { pageTitle: 'storeApp.users.home.title' },
        loadChildren: () => import('./users/users.module').then(m => m.UsersModule),
      },
      {
        path: 'addresses',
        data: { pageTitle: 'storeApp.addresses.home.title' },
        loadChildren: () => import('./addresses/addresses.module').then(m => m.AddressesModule),
      },
      {
        path: 'cards',
        data: { pageTitle: 'storeApp.cards.home.title' },
        loadChildren: () => import('./cards/cards.module').then(m => m.CardsModule),
      },
      {
        path: 'orders',
        data: { pageTitle: 'storeApp.orders.home.title' },
        loadChildren: () => import('./orders/orders.module').then(m => m.OrdersModule),
      },
      {
        path: 'order-products',
        data: { pageTitle: 'storeApp.orderProducts.home.title' },
        loadChildren: () => import('./order-products/order-products.module').then(m => m.OrderProductsModule),
      },
      {
        path: 'products',
        data: { pageTitle: 'storeApp.products.home.title' },
        loadChildren: () => import('./products/products.module').then(m => m.ProductsModule),
      },
      {
        path: 'wishlist',
        data: { pageTitle: 'storeApp.wishlist.home.title' },
        loadChildren: () => import('./wishlist/wishlist.module').then(m => m.WishlistModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
