jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IOrderProducts, OrderProducts } from '../order-products.model';
import { OrderProductsService } from '../service/order-products.service';

import { OrderProductsRoutingResolveService } from './order-products-routing-resolve.service';

describe('Service Tests', () => {
  describe('OrderProducts routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: OrderProductsRoutingResolveService;
    let service: OrderProductsService;
    let resultOrderProducts: IOrderProducts | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(OrderProductsRoutingResolveService);
      service = TestBed.inject(OrderProductsService);
      resultOrderProducts = undefined;
    });

    describe('resolve', () => {
      it('should return IOrderProducts returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOrderProducts = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultOrderProducts).toEqual({ id: 123 });
      });

      it('should return new IOrderProducts if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOrderProducts = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultOrderProducts).toEqual(new OrderProducts());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as OrderProducts })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOrderProducts = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultOrderProducts).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
