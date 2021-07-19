import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IOrders, Orders } from '../orders.model';
import { OrdersService } from '../service/orders.service';
import { IUsers } from 'app/entities/users/users.model';
import { UsersService } from 'app/entities/users/service/users.service';

@Component({
  selector: 'jhi-orders-update',
  templateUrl: './orders-update.component.html',
})
export class OrdersUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUsers[] = [];

  editForm = this.fb.group({
    id: [],
    idOrder: [],
    idOrderProducts: [],
    total: [],
    idOrders: [],
  });

  constructor(
    protected ordersService: OrdersService,
    protected usersService: UsersService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orders }) => {
      this.updateForm(orders);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orders = this.createFromForm();
    if (orders.id !== undefined) {
      this.subscribeToSaveResponse(this.ordersService.update(orders));
    } else {
      this.subscribeToSaveResponse(this.ordersService.create(orders));
    }
  }

  trackUsersById(index: number, item: IUsers): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrders>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(orders: IOrders): void {
    this.editForm.patchValue({
      id: orders.id,
      idOrder: orders.idOrder,
      idOrderProducts: orders.idOrderProducts,
      total: orders.total,
      idOrders: orders.idOrders,
    });

    this.usersSharedCollection = this.usersService.addUsersToCollectionIfMissing(this.usersSharedCollection, orders.idOrders);
  }

  protected loadRelationshipsOptions(): void {
    this.usersService
      .query()
      .pipe(map((res: HttpResponse<IUsers[]>) => res.body ?? []))
      .pipe(map((users: IUsers[]) => this.usersService.addUsersToCollectionIfMissing(users, this.editForm.get('idOrders')!.value)))
      .subscribe((users: IUsers[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IOrders {
    return {
      ...new Orders(),
      id: this.editForm.get(['id'])!.value,
      idOrder: this.editForm.get(['idOrder'])!.value,
      idOrderProducts: this.editForm.get(['idOrderProducts'])!.value,
      total: this.editForm.get(['total'])!.value,
      idOrders: this.editForm.get(['idOrders'])!.value,
    };
  }
}
