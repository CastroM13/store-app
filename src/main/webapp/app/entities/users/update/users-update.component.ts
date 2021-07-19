import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IUsers, Users } from '../users.model';
import { UsersService } from '../service/users.service';

@Component({
  selector: 'jhi-users-update',
  templateUrl: './users-update.component.html',
})
export class UsersUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    idUser: [],
    name: [],
    email: [],
    password: [],
    idAdresses: [],
    idCards: [],
    idOrders: [],
    idWishlist: [],
  });

  constructor(protected usersService: UsersService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ users }) => {
      this.updateForm(users);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const users = this.createFromForm();
    if (users.id !== undefined) {
      this.subscribeToSaveResponse(this.usersService.update(users));
    } else {
      this.subscribeToSaveResponse(this.usersService.create(users));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUsers>>): void {
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

  protected updateForm(users: IUsers): void {
    this.editForm.patchValue({
      id: users.id,
      idUser: users.idUser,
      name: users.name,
      email: users.email,
      password: users.password,
      idAdresses: users.idAdresses,
      idCards: users.idCards,
      idOrders: users.idOrders,
      idWishlist: users.idWishlist,
    });
  }

  protected createFromForm(): IUsers {
    return {
      ...new Users(),
      id: this.editForm.get(['id'])!.value,
      idUser: this.editForm.get(['idUser'])!.value,
      name: this.editForm.get(['name'])!.value,
      email: this.editForm.get(['email'])!.value,
      password: this.editForm.get(['password'])!.value,
      idAdresses: this.editForm.get(['idAdresses'])!.value,
      idCards: this.editForm.get(['idCards'])!.value,
      idOrders: this.editForm.get(['idOrders'])!.value,
      idWishlist: this.editForm.get(['idWishlist'])!.value,
    };
  }
}
