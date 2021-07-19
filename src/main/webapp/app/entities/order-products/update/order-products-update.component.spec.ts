jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { OrderProductsService } from '../service/order-products.service';
import { IOrderProducts, OrderProducts } from '../order-products.model';
import { IOrders } from 'app/entities/orders/orders.model';
import { OrdersService } from 'app/entities/orders/service/orders.service';
import { IProducts } from 'app/entities/products/products.model';
import { ProductsService } from 'app/entities/products/service/products.service';

import { OrderProductsUpdateComponent } from './order-products-update.component';

describe('Component Tests', () => {
  describe('OrderProducts Management Update Component', () => {
    let comp: OrderProductsUpdateComponent;
    let fixture: ComponentFixture<OrderProductsUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let orderProductsService: OrderProductsService;
    let ordersService: OrdersService;
    let productsService: ProductsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [OrderProductsUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(OrderProductsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OrderProductsUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      orderProductsService = TestBed.inject(OrderProductsService);
      ordersService = TestBed.inject(OrdersService);
      productsService = TestBed.inject(ProductsService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Orders query and add missing value', () => {
        const orderProducts: IOrderProducts = { id: 456 };
        const orders: IOrders[] = [{ id: 59801 }];
        orderProducts.orders = orders;

        const ordersCollection: IOrders[] = [{ id: 20717 }];
        jest.spyOn(ordersService, 'query').mockReturnValue(of(new HttpResponse({ body: ordersCollection })));
        const additionalOrders = [...orders];
        const expectedCollection: IOrders[] = [...additionalOrders, ...ordersCollection];
        jest.spyOn(ordersService, 'addOrdersToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ orderProducts });
        comp.ngOnInit();

        expect(ordersService.query).toHaveBeenCalled();
        expect(ordersService.addOrdersToCollectionIfMissing).toHaveBeenCalledWith(ordersCollection, ...additionalOrders);
        expect(comp.ordersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Products query and add missing value', () => {
        const orderProducts: IOrderProducts = { id: 456 };
        const products: IProducts[] = [{ id: 21156 }];
        orderProducts.products = products;

        const productsCollection: IProducts[] = [{ id: 7793 }];
        jest.spyOn(productsService, 'query').mockReturnValue(of(new HttpResponse({ body: productsCollection })));
        const additionalProducts = [...products];
        const expectedCollection: IProducts[] = [...additionalProducts, ...productsCollection];
        jest.spyOn(productsService, 'addProductsToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ orderProducts });
        comp.ngOnInit();

        expect(productsService.query).toHaveBeenCalled();
        expect(productsService.addProductsToCollectionIfMissing).toHaveBeenCalledWith(productsCollection, ...additionalProducts);
        expect(comp.productsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const orderProducts: IOrderProducts = { id: 456 };
        const orders: IOrders = { id: 96226 };
        orderProducts.orders = [orders];
        const products: IProducts = { id: 70199 };
        orderProducts.products = [products];

        activatedRoute.data = of({ orderProducts });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(orderProducts));
        expect(comp.ordersSharedCollection).toContain(orders);
        expect(comp.productsSharedCollection).toContain(products);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<OrderProducts>>();
        const orderProducts = { id: 123 };
        jest.spyOn(orderProductsService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ orderProducts });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: orderProducts }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(orderProductsService.update).toHaveBeenCalledWith(orderProducts);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<OrderProducts>>();
        const orderProducts = new OrderProducts();
        jest.spyOn(orderProductsService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ orderProducts });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: orderProducts }));
        saveSubject.complete();

        // THEN
        expect(orderProductsService.create).toHaveBeenCalledWith(orderProducts);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<OrderProducts>>();
        const orderProducts = { id: 123 };
        jest.spyOn(orderProductsService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ orderProducts });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(orderProductsService.update).toHaveBeenCalledWith(orderProducts);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackOrdersById', () => {
        it('Should return tracked Orders primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackOrdersById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackProductsById', () => {
        it('Should return tracked Products primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackProductsById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedOrders', () => {
        it('Should return option if no Orders is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedOrders(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Orders for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedOrders(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Orders is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedOrders(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });

      describe('getSelectedProducts', () => {
        it('Should return option if no Products is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedProducts(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Products for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedProducts(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Products is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedProducts(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
