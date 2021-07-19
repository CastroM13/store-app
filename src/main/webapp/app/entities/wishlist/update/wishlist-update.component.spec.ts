jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { WishlistService } from '../service/wishlist.service';
import { IWishlist, Wishlist } from '../wishlist.model';
import { IUsers } from 'app/entities/users/users.model';
import { UsersService } from 'app/entities/users/service/users.service';

import { WishlistUpdateComponent } from './wishlist-update.component';

describe('Component Tests', () => {
  describe('Wishlist Management Update Component', () => {
    let comp: WishlistUpdateComponent;
    let fixture: ComponentFixture<WishlistUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let wishlistService: WishlistService;
    let usersService: UsersService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [WishlistUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(WishlistUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(WishlistUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      wishlistService = TestBed.inject(WishlistService);
      usersService = TestBed.inject(UsersService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Users query and add missing value', () => {
        const wishlist: IWishlist = { id: 456 };
        const idWishlist: IUsers = { id: 29924 };
        wishlist.idWishlist = idWishlist;

        const usersCollection: IUsers[] = [{ id: 71875 }];
        jest.spyOn(usersService, 'query').mockReturnValue(of(new HttpResponse({ body: usersCollection })));
        const additionalUsers = [idWishlist];
        const expectedCollection: IUsers[] = [...additionalUsers, ...usersCollection];
        jest.spyOn(usersService, 'addUsersToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ wishlist });
        comp.ngOnInit();

        expect(usersService.query).toHaveBeenCalled();
        expect(usersService.addUsersToCollectionIfMissing).toHaveBeenCalledWith(usersCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const wishlist: IWishlist = { id: 456 };
        const idWishlist: IUsers = { id: 36477 };
        wishlist.idWishlist = idWishlist;

        activatedRoute.data = of({ wishlist });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(wishlist));
        expect(comp.usersSharedCollection).toContain(idWishlist);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Wishlist>>();
        const wishlist = { id: 123 };
        jest.spyOn(wishlistService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ wishlist });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: wishlist }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(wishlistService.update).toHaveBeenCalledWith(wishlist);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Wishlist>>();
        const wishlist = new Wishlist();
        jest.spyOn(wishlistService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ wishlist });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: wishlist }));
        saveSubject.complete();

        // THEN
        expect(wishlistService.create).toHaveBeenCalledWith(wishlist);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Wishlist>>();
        const wishlist = { id: 123 };
        jest.spyOn(wishlistService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ wishlist });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(wishlistService.update).toHaveBeenCalledWith(wishlist);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUsersById', () => {
        it('Should return tracked Users primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUsersById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
