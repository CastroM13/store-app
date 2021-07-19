jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IOrders, Orders } from '../orders.model';
import { OrdersService } from '../service/orders.service';

import { OrdersRoutingResolveService } from './orders-routing-resolve.service';

describe('Service Tests', () => {
  describe('Orders routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: OrdersRoutingResolveService;
    let service: OrdersService;
    let resultOrders: IOrders | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(OrdersRoutingResolveService);
      service = TestBed.inject(OrdersService);
      resultOrders = undefined;
    });

    describe('resolve', () => {
      it('should return IOrders returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOrders = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultOrders).toEqual({ id: 123 });
      });

      it('should return new IOrders if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOrders = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultOrders).toEqual(new Orders());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Orders })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOrders = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultOrders).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
