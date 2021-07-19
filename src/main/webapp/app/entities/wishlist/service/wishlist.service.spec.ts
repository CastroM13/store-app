import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IWishlist, Wishlist } from '../wishlist.model';

import { WishlistService } from './wishlist.service';

describe('Service Tests', () => {
  describe('Wishlist Service', () => {
    let service: WishlistService;
    let httpMock: HttpTestingController;
    let elemDefault: IWishlist;
    let expectedResult: IWishlist | IWishlist[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(WishlistService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        idWishlist: 0,
        idUser: 0,
        idProducts: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Wishlist', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Wishlist()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Wishlist', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            idWishlist: 1,
            idUser: 1,
            idProducts: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Wishlist', () => {
        const patchObject = Object.assign(
          {
            idUser: 1,
            idProducts: 1,
          },
          new Wishlist()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Wishlist', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            idWishlist: 1,
            idUser: 1,
            idProducts: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Wishlist', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addWishlistToCollectionIfMissing', () => {
        it('should add a Wishlist to an empty array', () => {
          const wishlist: IWishlist = { id: 123 };
          expectedResult = service.addWishlistToCollectionIfMissing([], wishlist);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(wishlist);
        });

        it('should not add a Wishlist to an array that contains it', () => {
          const wishlist: IWishlist = { id: 123 };
          const wishlistCollection: IWishlist[] = [
            {
              ...wishlist,
            },
            { id: 456 },
          ];
          expectedResult = service.addWishlistToCollectionIfMissing(wishlistCollection, wishlist);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Wishlist to an array that doesn't contain it", () => {
          const wishlist: IWishlist = { id: 123 };
          const wishlistCollection: IWishlist[] = [{ id: 456 }];
          expectedResult = service.addWishlistToCollectionIfMissing(wishlistCollection, wishlist);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(wishlist);
        });

        it('should add only unique Wishlist to an array', () => {
          const wishlistArray: IWishlist[] = [{ id: 123 }, { id: 456 }, { id: 14837 }];
          const wishlistCollection: IWishlist[] = [{ id: 123 }];
          expectedResult = service.addWishlistToCollectionIfMissing(wishlistCollection, ...wishlistArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const wishlist: IWishlist = { id: 123 };
          const wishlist2: IWishlist = { id: 456 };
          expectedResult = service.addWishlistToCollectionIfMissing([], wishlist, wishlist2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(wishlist);
          expect(expectedResult).toContain(wishlist2);
        });

        it('should accept null and undefined values', () => {
          const wishlist: IWishlist = { id: 123 };
          expectedResult = service.addWishlistToCollectionIfMissing([], null, wishlist, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(wishlist);
        });

        it('should return initial array if no Wishlist is added', () => {
          const wishlistCollection: IWishlist[] = [{ id: 123 }];
          expectedResult = service.addWishlistToCollectionIfMissing(wishlistCollection, undefined, null);
          expect(expectedResult).toEqual(wishlistCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
