import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOrderProducts, OrderProducts } from '../order-products.model';

import { OrderProductsService } from './order-products.service';

describe('Service Tests', () => {
  describe('OrderProducts Service', () => {
    let service: OrderProductsService;
    let httpMock: HttpTestingController;
    let elemDefault: IOrderProducts;
    let expectedResult: IOrderProducts | IOrderProducts[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(OrderProductsService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        idOrderProducts: 0,
        idOrder: 0,
        idProduct: 0,
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

      it('should create a OrderProducts', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new OrderProducts()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a OrderProducts', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            idOrderProducts: 1,
            idOrder: 1,
            idProduct: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a OrderProducts', () => {
        const patchObject = Object.assign(
          {
            idOrderProducts: 1,
            idOrder: 1,
          },
          new OrderProducts()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of OrderProducts', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            idOrderProducts: 1,
            idOrder: 1,
            idProduct: 1,
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

      it('should delete a OrderProducts', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addOrderProductsToCollectionIfMissing', () => {
        it('should add a OrderProducts to an empty array', () => {
          const orderProducts: IOrderProducts = { id: 123 };
          expectedResult = service.addOrderProductsToCollectionIfMissing([], orderProducts);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(orderProducts);
        });

        it('should not add a OrderProducts to an array that contains it', () => {
          const orderProducts: IOrderProducts = { id: 123 };
          const orderProductsCollection: IOrderProducts[] = [
            {
              ...orderProducts,
            },
            { id: 456 },
          ];
          expectedResult = service.addOrderProductsToCollectionIfMissing(orderProductsCollection, orderProducts);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a OrderProducts to an array that doesn't contain it", () => {
          const orderProducts: IOrderProducts = { id: 123 };
          const orderProductsCollection: IOrderProducts[] = [{ id: 456 }];
          expectedResult = service.addOrderProductsToCollectionIfMissing(orderProductsCollection, orderProducts);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(orderProducts);
        });

        it('should add only unique OrderProducts to an array', () => {
          const orderProductsArray: IOrderProducts[] = [{ id: 123 }, { id: 456 }, { id: 55519 }];
          const orderProductsCollection: IOrderProducts[] = [{ id: 123 }];
          expectedResult = service.addOrderProductsToCollectionIfMissing(orderProductsCollection, ...orderProductsArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const orderProducts: IOrderProducts = { id: 123 };
          const orderProducts2: IOrderProducts = { id: 456 };
          expectedResult = service.addOrderProductsToCollectionIfMissing([], orderProducts, orderProducts2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(orderProducts);
          expect(expectedResult).toContain(orderProducts2);
        });

        it('should accept null and undefined values', () => {
          const orderProducts: IOrderProducts = { id: 123 };
          expectedResult = service.addOrderProductsToCollectionIfMissing([], null, orderProducts, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(orderProducts);
        });

        it('should return initial array if no OrderProducts is added', () => {
          const orderProductsCollection: IOrderProducts[] = [{ id: 123 }];
          expectedResult = service.addOrderProductsToCollectionIfMissing(orderProductsCollection, undefined, null);
          expect(expectedResult).toEqual(orderProductsCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
