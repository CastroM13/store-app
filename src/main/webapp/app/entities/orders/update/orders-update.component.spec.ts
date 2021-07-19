jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { OrdersService } from '../service/orders.service';
import { IOrders, Orders } from '../orders.model';
import { IUsers } from 'app/entities/users/users.model';
import { UsersService } from 'app/entities/users/service/users.service';

import { OrdersUpdateComponent } from './orders-update.component';

describe('Component Tests', () => {
  describe('Orders Management Update Component', () => {
    let comp: OrdersUpdateComponent;
    let fixture: ComponentFixture<OrdersUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let ordersService: OrdersService;
    let usersService: UsersService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [OrdersUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(OrdersUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OrdersUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      ordersService = TestBed.inject(OrdersService);
      usersService = TestBed.inject(UsersService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Users query and add missing value', () => {
        const orders: IOrders = { id: 456 };
        const idOrders: IUsers = { id: 20469 };
        orders.idOrders = idOrders;

        const usersCollection: IUsers[] = [{ id: 12185 }];
        jest.spyOn(usersService, 'query').mockReturnValue(of(new HttpResponse({ body: usersCollection })));
        const additionalUsers = [idOrders];
        const expectedCollection: IUsers[] = [...additionalUsers, ...usersCollection];
        jest.spyOn(usersService, 'addUsersToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ orders });
        comp.ngOnInit();

        expect(usersService.query).toHaveBeenCalled();
        expect(usersService.addUsersToCollectionIfMissing).toHaveBeenCalledWith(usersCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const orders: IOrders = { id: 456 };
        const idOrders: IUsers = { id: 28045 };
        orders.idOrders = idOrders;

        activatedRoute.data = of({ orders });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(orders));
        expect(comp.usersSharedCollection).toContain(idOrders);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Orders>>();
        const orders = { id: 123 };
        jest.spyOn(ordersService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ orders });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: orders }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(ordersService.update).toHaveBeenCalledWith(orders);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Orders>>();
        const orders = new Orders();
        jest.spyOn(ordersService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ orders });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: orders }));
        saveSubject.complete();

        // THEN
        expect(ordersService.create).toHaveBeenCalledWith(orders);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Orders>>();
        const orders = { id: 123 };
        jest.spyOn(ordersService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ orders });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(ordersService.update).toHaveBeenCalledWith(orders);
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
