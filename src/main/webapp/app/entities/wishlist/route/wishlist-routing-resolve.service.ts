import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWishlist, Wishlist } from '../wishlist.model';
import { WishlistService } from '../service/wishlist.service';

@Injectable({ providedIn: 'root' })
export class WishlistRoutingResolveService implements Resolve<IWishlist> {
  constructor(protected service: WishlistService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IWishlist> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((wishlist: HttpResponse<Wishlist>) => {
          if (wishlist.body) {
            return of(wishlist.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Wishlist());
  }
}
