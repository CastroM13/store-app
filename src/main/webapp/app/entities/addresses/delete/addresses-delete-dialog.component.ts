import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAddresses } from '../addresses.model';
import { AddressesService } from '../service/addresses.service';

@Component({
  templateUrl: './addresses-delete-dialog.component.html',
})
export class AddressesDeleteDialogComponent {
  addresses?: IAddresses;

  constructor(protected addressesService: AddressesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.addressesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
