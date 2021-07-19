import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICards, Cards } from '../cards.model';
import { CardsService } from '../service/cards.service';

@Injectable({ providedIn: 'root' })
export class CardsRoutingResolveService implements Resolve<ICards> {
  constructor(protected service: CardsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICards> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cards: HttpResponse<Cards>) => {
          if (cards.body) {
            return of(cards.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Cards());
  }
}
