import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICards, Cards } from '../cards.model';
import { CardsService } from '../service/cards.service';
import { IUsers } from 'app/entities/users/users.model';
import { UsersService } from 'app/entities/users/service/users.service';

@Component({
  selector: 'jhi-cards-update',
  templateUrl: './cards-update.component.html',
})
export class CardsUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUsers[] = [];

  editForm = this.fb.group({
    id: [],
    idCards: [],
    identityDocument: [],
    cardNumber: [],
    securityCode: [],
    expirationDate: [],
    cardholder: [],
    parcelQuantity: [],
    idCards: [],
  });

  constructor(
    protected cardsService: CardsService,
    protected usersService: UsersService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cards }) => {
      this.updateForm(cards);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cards = this.createFromForm();
    if (cards.id !== undefined) {
      this.subscribeToSaveResponse(this.cardsService.update(cards));
    } else {
      this.subscribeToSaveResponse(this.cardsService.create(cards));
    }
  }

  trackUsersById(index: number, item: IUsers): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICards>>): void {
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

  protected updateForm(cards: ICards): void {
    this.editForm.patchValue({
      id: cards.id,
      idCards: cards.idCards,
      identityDocument: cards.identityDocument,
      cardNumber: cards.cardNumber,
      securityCode: cards.securityCode,
      expirationDate: cards.expirationDate,
      cardholder: cards.cardholder,
      parcelQuantity: cards.parcelQuantity,
      idCards: cards.idCards,
    });

    this.usersSharedCollection = this.usersService.addUsersToCollectionIfMissing(this.usersSharedCollection, cards.idCards);
  }

  protected loadRelationshipsOptions(): void {
    this.usersService
      .query()
      .pipe(map((res: HttpResponse<IUsers[]>) => res.body ?? []))
      .pipe(map((users: IUsers[]) => this.usersService.addUsersToCollectionIfMissing(users, this.editForm.get('idCards')!.value)))
      .subscribe((users: IUsers[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): ICards {
    return {
      ...new Cards(),
      id: this.editForm.get(['id'])!.value,
      idCards: this.editForm.get(['idCards'])!.value,
      identityDocument: this.editForm.get(['identityDocument'])!.value,
      cardNumber: this.editForm.get(['cardNumber'])!.value,
      securityCode: this.editForm.get(['securityCode'])!.value,
      expirationDate: this.editForm.get(['expirationDate'])!.value,
      cardholder: this.editForm.get(['cardholder'])!.value,
      parcelQuantity: this.editForm.get(['parcelQuantity'])!.value,
      idCards: this.editForm.get(['idCards'])!.value,
    };
  }
}
