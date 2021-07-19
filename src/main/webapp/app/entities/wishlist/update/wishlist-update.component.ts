import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IWishlist, Wishlist } from '../wishlist.model';
import { WishlistService } from '../service/wishlist.service';
import { IUsers } from 'app/entities/users/users.model';
import { UsersService } from 'app/entities/users/service/users.service';

@Component({
  selector: 'jhi-wishlist-update',
  templateUrl: './wishlist-update.component.html',
})
export class WishlistUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUsers[] = [];

  editForm = this.fb.group({
    id: [],
    idWishlist: [],
    idUser: [],
    idProducts: [],
    idWishlist: [],
  });

  constructor(
    protected wishlistService: WishlistService,
    protected usersService: UsersService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ wishlist }) => {
      this.updateForm(wishlist);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const wishlist = this.createFromForm();
    if (wishlist.id !== undefined) {
      this.subscribeToSaveResponse(this.wishlistService.update(wishlist));
    } else {
      this.subscribeToSaveResponse(this.wishlistService.create(wishlist));
    }
  }

  trackUsersById(index: number, item: IUsers): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWishlist>>): void {
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

  protected updateForm(wishlist: IWishlist): void {
    this.editForm.patchValue({
      id: wishlist.id,
      idWishlist: wishlist.idWishlist,
      idUser: wishlist.idUser,
      idProducts: wishlist.idProducts,
      idWishlist: wishlist.idWishlist,
    });

    this.usersSharedCollection = this.usersService.addUsersToCollectionIfMissing(this.usersSharedCollection, wishlist.idWishlist);
  }

  protected loadRelationshipsOptions(): void {
    this.usersService
      .query()
      .pipe(map((res: HttpResponse<IUsers[]>) => res.body ?? []))
      .pipe(map((users: IUsers[]) => this.usersService.addUsersToCollectionIfMissing(users, this.editForm.get('idWishlist')!.value)))
      .subscribe((users: IUsers[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IWishlist {
    return {
      ...new Wishlist(),
      id: this.editForm.get(['id'])!.value,
      idWishlist: this.editForm.get(['idWishlist'])!.value,
      idUser: this.editForm.get(['idUser'])!.value,
      idProducts: this.editForm.get(['idProducts'])!.value,
      idWishlist: this.editForm.get(['idWishlist'])!.value,
    };
  }
}
