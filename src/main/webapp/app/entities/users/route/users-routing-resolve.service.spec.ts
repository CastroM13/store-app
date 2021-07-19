jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IUsers, Users } from '../users.model';
import { UsersService } from '../service/users.service';

import { UsersRoutingResolveService } from './users-routing-resolve.service';

describe('Service Tests', () => {
  describe('Users routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: UsersRoutingResolveService;
    let service: UsersService;
    let resultUsers: IUsers | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(UsersRoutingResolveService);
      service = TestBed.inject(UsersService);
      resultUsers = undefined;
    });

    describe('resolve', () => {
      it('should return IUsers returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUsers = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultUsers).toEqual({ id: 123 });
      });

      it('should return new IUsers if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUsers = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultUsers).toEqual(new Users());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Users })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUsers = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultUsers).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
