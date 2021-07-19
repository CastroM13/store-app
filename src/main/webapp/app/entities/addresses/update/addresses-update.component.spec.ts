jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { AddressesService } from '../service/addresses.service';
import { IAddresses, Addresses } from '../addresses.model';
import { IUsers } from 'app/entities/users/users.model';
import { UsersService } from 'app/entities/users/service/users.service';

import { AddressesUpdateComponent } from './addresses-update.component';

describe('Component Tests', () => {
  describe('Addresses Management Update Component', () => {
    let comp: AddressesUpdateComponent;
    let fixture: ComponentFixture<AddressesUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let addressesService: AddressesService;
    let usersService: UsersService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AddressesUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(AddressesUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AddressesUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      addressesService = TestBed.inject(AddressesService);
      usersService = TestBed.inject(UsersService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Users query and add missing value', () => {
        const addresses: IAddresses = { id: 456 };
        const idAdresses: IUsers = { id: 92896 };
        addresses.idAdresses = idAdresses;

        const usersCollection: IUsers[] = [{ id: 52660 }];
        jest.spyOn(usersService, 'query').mockReturnValue(of(new HttpResponse({ body: usersCollection })));
        const additionalUsers = [idAdresses];
        const expectedCollection: IUsers[] = [...additionalUsers, ...usersCollection];
        jest.spyOn(usersService, 'addUsersToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ addresses });
        comp.ngOnInit();

        expect(usersService.query).toHaveBeenCalled();
        expect(usersService.addUsersToCollectionIfMissing).toHaveBeenCalledWith(usersCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const addresses: IAddresses = { id: 456 };
        const idAdresses: IUsers = { id: 36807 };
        addresses.idAdresses = idAdresses;

        activatedRoute.data = of({ addresses });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(addresses));
        expect(comp.usersSharedCollection).toContain(idAdresses);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Addresses>>();
        const addresses = { id: 123 };
        jest.spyOn(addressesService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ addresses });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: addresses }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(addressesService.update).toHaveBeenCalledWith(addresses);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Addresses>>();
        const addresses = new Addresses();
        jest.spyOn(addressesService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ addresses });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: addresses }));
        saveSubject.complete();

        // THEN
        expect(addressesService.create).toHaveBeenCalledWith(addresses);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Addresses>>();
        const addresses = { id: 123 };
        jest.spyOn(addressesService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ addresses });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(addressesService.update).toHaveBeenCalledWith(addresses);
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
