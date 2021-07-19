jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IAddresses, Addresses } from '../addresses.model';
import { AddressesService } from '../service/addresses.service';

import { AddressesRoutingResolveService } from './addresses-routing-resolve.service';

describe('Service Tests', () => {
  describe('Addresses routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: AddressesRoutingResolveService;
    let service: AddressesService;
    let resultAddresses: IAddresses | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(AddressesRoutingResolveService);
      service = TestBed.inject(AddressesService);
      resultAddresses = undefined;
    });

    describe('resolve', () => {
      it('should return IAddresses returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAddresses = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultAddresses).toEqual({ id: 123 });
      });

      it('should return new IAddresses if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAddresses = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultAddresses).toEqual(new Addresses());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Addresses })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAddresses = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultAddresses).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
