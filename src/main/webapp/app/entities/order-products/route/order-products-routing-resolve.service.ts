import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrderProducts, OrderProducts } from '../order-products.model';
import { OrderProductsService } from '../service/order-products.service';

@Injectable({ providedIn: 'root' })
export class OrderProductsRoutingResolveService implements Resolve<IOrderProducts> {
  constructor(protected service: OrderProductsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrderProducts> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((orderProducts: HttpResponse<OrderProducts>) => {
          if (orderProducts.body) {
            return of(orderProducts.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new OrderProducts());
  }
}
