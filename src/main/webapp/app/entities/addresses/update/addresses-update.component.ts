import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAddresses, Addresses } from '../addresses.model';
import { AddressesService } from '../service/addresses.service';
import { IUsers } from 'app/entities/users/users.model';
import { UsersService } from 'app/entities/users/service/users.service';

@Component({
  selector: 'jhi-addresses-update',
  templateUrl: './addresses-update.component.html',
})
export class AddressesUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUsers[] = [];

  editForm = this.fb.group({
    id: [],
    idAdresses: [],
    country: [],
    streetAddress: [],
    postalCode: [],
    city: [],
    stateProvince: [],
    idAdresses: [],
  });

  constructor(
    protected addressesService: AddressesService,
    protected usersService: UsersService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ addresses }) => {
      this.updateForm(addresses);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const addresses = this.createFromForm();
    if (addresses.id !== undefined) {
      this.subscribeToSaveResponse(this.addressesService.update(addresses));
    } else {
      this.subscribeToSaveResponse(this.addressesService.create(addresses));
    }
  }

  trackUsersById(index: number, item: IUsers): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAddresses>>): void {
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

  protected updateForm(addresses: IAddresses): void {
    this.editForm.patchValue({
      id: addresses.id,
      idAdresses: addresses.idAdresses,
      country: addresses.country,
      streetAddress: addresses.streetAddress,
      postalCode: addresses.postalCode,
      city: addresses.city,
      stateProvince: addresses.stateProvince,
      idAdresses: addresses.idAdresses,
    });

    this.usersSharedCollection = this.usersService.addUsersToCollectionIfMissing(this.usersSharedCollection, addresses.idAdresses);
  }

  protected loadRelationshipsOptions(): void {
    this.usersService
      .query()
      .pipe(map((res: HttpResponse<IUsers[]>) => res.body ?? []))
      .pipe(map((users: IUsers[]) => this.usersService.addUsersToCollectionIfMissing(users, this.editForm.get('idAdresses')!.value)))
      .subscribe((users: IUsers[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IAddresses {
    return {
      ...new Addresses(),
      id: this.editForm.get(['id'])!.value,
      idAdresses: this.editForm.get(['idAdresses'])!.value,
      country: this.editForm.get(['country'])!.value,
      streetAddress: this.editForm.get(['streetAddress'])!.value,
      postalCode: this.editForm.get(['postalCode'])!.value,
      city: this.editForm.get(['city'])!.value,
      stateProvince: this.editForm.get(['stateProvince'])!.value,
      idAdresses: this.editForm.get(['idAdresses'])!.value,
    };
  }
}
