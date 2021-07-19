import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CardsComponent } from '../list/cards.component';
import { CardsDetailComponent } from '../detail/cards-detail.component';
import { CardsUpdateComponent } from '../update/cards-update.component';
import { CardsRoutingResolveService } from './cards-routing-resolve.service';

const cardsRoute: Routes = [
  {
    path: '',
    component: CardsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CardsDetailComponent,
    resolve: {
      cards: CardsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CardsUpdateComponent,
    resolve: {
      cards: CardsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CardsUpdateComponent,
    resolve: {
      cards: CardsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cardsRoute)],
  exports: [RouterModule],
})
export class CardsRoutingModule {}
