jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IProducts, Products } from '../products.model';
import { ProductsService } from '../service/products.service';

import { ProductsRoutingResolveService } from './products-routing-resolve.service';

describe('Service Tests', () => {
  describe('Products routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ProductsRoutingResolveService;
    let service: ProductsService;
    let resultProducts: IProducts | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ProductsRoutingResolveService);
      service = TestBed.inject(ProductsService);
      resultProducts = undefined;
    });

    describe('resolve', () => {
      it('should return IProducts returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultProducts = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultProducts).toEqual({ id: 123 });
      });

      it('should return new IProducts if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultProducts = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultProducts).toEqual(new Products());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Products })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultProducts = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultProducts).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
