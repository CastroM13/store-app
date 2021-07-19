import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CardsComponent } from './list/cards.component';
import { CardsDetailComponent } from './detail/cards-detail.component';
import { CardsUpdateComponent } from './update/cards-update.component';
import { CardsDeleteDialogComponent } from './delete/cards-delete-dialog.component';
import { CardsRoutingModule } from './route/cards-routing.module';

@NgModule({
  imports: [SharedModule, CardsRoutingModule],
  declarations: [CardsComponent, CardsDetailComponent, CardsUpdateComponent, CardsDeleteDialogComponent],
  entryComponents: [CardsDeleteDialogComponent],
})
export class CardsModule {}
