import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrderProducts } from '../order-products.model';
import { OrderProductsService } from '../service/order-products.service';

@Component({
  templateUrl: './order-products-delete-dialog.component.html',
})
export class OrderProductsDeleteDialogComponent {
  orderProducts?: IOrderProducts;

  constructor(protected orderProductsService: OrderProductsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.orderProductsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
