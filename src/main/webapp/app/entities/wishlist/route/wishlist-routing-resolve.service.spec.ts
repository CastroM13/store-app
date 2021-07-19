jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IWishlist, Wishlist } from '../wishlist.model';
import { WishlistService } from '../service/wishlist.service';

import { WishlistRoutingResolveService } from './wishlist-routing-resolve.service';

describe('Service Tests', () => {
  describe('Wishlist routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: WishlistRoutingResolveService;
    let service: WishlistService;
    let resultWishlist: IWishlist | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(WishlistRoutingResolveService);
      service = TestBed.inject(WishlistService);
      resultWishlist = undefined;
    });

    describe('resolve', () => {
      it('should return IWishlist returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultWishlist = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultWishlist).toEqual({ id: 123 });
      });

      it('should return new IWishlist if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultWishlist = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultWishlist).toEqual(new Wishlist());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Wishlist })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultWishlist = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultWishlist).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
