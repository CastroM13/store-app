import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOrders, Orders } from '../orders.model';

import { OrdersService } from './orders.service';

describe('Service Tests', () => {
  describe('Orders Service', () => {
    let service: OrdersService;
    let httpMock: HttpTestingController;
    let elemDefault: IOrders;
    let expectedResult: IOrders | IOrders[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(OrdersService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        idOrder: 0,
        idOrderProducts: 0,
        total: 0,
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

      it('should create a Orders', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Orders()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Orders', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            idOrder: 1,
            idOrderProducts: 1,
            total: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Orders', () => {
        const patchObject = Object.assign(
          {
            idOrderProducts: 1,
            total: 1,
          },
          new Orders()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Orders', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            idOrder: 1,
            idOrderProducts: 1,
            total: 1,
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

      it('should delete a Orders', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addOrdersToCollectionIfMissing', () => {
        it('should add a Orders to an empty array', () => {
          const orders: IOrders = { id: 123 };
          expectedResult = service.addOrdersToCollectionIfMissing([], orders);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(orders);
        });

        it('should not add a Orders to an array that contains it', () => {
          const orders: IOrders = { id: 123 };
          const ordersCollection: IOrders[] = [
            {
              ...orders,
            },
            { id: 456 },
          ];
          expectedResult = service.addOrdersToCollectionIfMissing(ordersCollection, orders);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Orders to an array that doesn't contain it", () => {
          const orders: IOrders = { id: 123 };
          const ordersCollection: IOrders[] = [{ id: 456 }];
          expectedResult = service.addOrdersToCollectionIfMissing(ordersCollection, orders);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(orders);
        });

        it('should add only unique Orders to an array', () => {
          const ordersArray: IOrders[] = [{ id: 123 }, { id: 456 }, { id: 11720 }];
          const ordersCollection: IOrders[] = [{ id: 123 }];
          expectedResult = service.addOrdersToCollectionIfMissing(ordersCollection, ...ordersArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const orders: IOrders = { id: 123 };
          const orders2: IOrders = { id: 456 };
          expectedResult = service.addOrdersToCollectionIfMissing([], orders, orders2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(orders);
          expect(expectedResult).toContain(orders2);
        });

        it('should accept null and undefined values', () => {
          const orders: IOrders = { id: 123 };
          expectedResult = service.addOrdersToCollectionIfMissing([], null, orders, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(orders);
        });

        it('should return initial array if no Orders is added', () => {
          const ordersCollection: IOrders[] = [{ id: 123 }];
          expectedResult = service.addOrdersToCollectionIfMissing(ordersCollection, undefined, null);
          expect(expectedResult).toEqual(ordersCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
