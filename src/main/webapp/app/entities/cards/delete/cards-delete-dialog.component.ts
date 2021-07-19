import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICards } from '../cards.model';
import { CardsService } from '../service/cards.service';

@Component({
  templateUrl: './cards-delete-dialog.component.html',
})
export class CardsDeleteDialogComponent {
  cards?: ICards;

  constructor(protected cardsService: CardsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cardsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
