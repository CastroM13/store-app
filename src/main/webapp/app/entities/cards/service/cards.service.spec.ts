import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICards, Cards } from '../cards.model';

import { CardsService } from './cards.service';

describe('Service Tests', () => {
  describe('Cards Service', () => {
    let service: CardsService;
    let httpMock: HttpTestingController;
    let elemDefault: ICards;
    let expectedResult: ICards | ICards[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CardsService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        idCards: 0,
        identityDocument: 'AAAAAAA',
        cardNumber: 'AAAAAAA',
        securityCode: 0,
        expirationDate: 'AAAAAAA',
        cardholder: 'AAAAAAA',
        parcelQuantity: 0,
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

      it('should create a Cards', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Cards()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Cards', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            idCards: 1,
            identityDocument: 'BBBBBB',
            cardNumber: 'BBBBBB',
            securityCode: 1,
            expirationDate: 'BBBBBB',
            cardholder: 'BBBBBB',
            parcelQuantity: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Cards', () => {
        const patchObject = Object.assign(
          {
            expirationDate: 'BBBBBB',
            cardholder: 'BBBBBB',
          },
          new Cards()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Cards', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            idCards: 1,
            identityDocument: 'BBBBBB',
            cardNumber: 'BBBBBB',
            securityCode: 1,
            expirationDate: 'BBBBBB',
            cardholder: 'BBBBBB',
            parcelQuantity: 1,
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

      it('should delete a Cards', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCardsToCollectionIfMissing', () => {
        it('should add a Cards to an empty array', () => {
          const cards: ICards = { id: 123 };
          expectedResult = service.addCardsToCollectionIfMissing([], cards);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(cards);
        });

        it('should not add a Cards to an array that contains it', () => {
          const cards: ICards = { id: 123 };
          const cardsCollection: ICards[] = [
            {
              ...cards,
            },
            { id: 456 },
          ];
          expectedResult = service.addCardsToCollectionIfMissing(cardsCollection, cards);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Cards to an array that doesn't contain it", () => {
          const cards: ICards = { id: 123 };
          const cardsCollection: ICards[] = [{ id: 456 }];
          expectedResult = service.addCardsToCollectionIfMissing(cardsCollection, cards);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(cards);
        });

        it('should add only unique Cards to an array', () => {
          const cardsArray: ICards[] = [{ id: 123 }, { id: 456 }, { id: 43800 }];
          const cardsCollection: ICards[] = [{ id: 123 }];
          expectedResult = service.addCardsToCollectionIfMissing(cardsCollection, ...cardsArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const cards: ICards = { id: 123 };
          const cards2: ICards = { id: 456 };
          expectedResult = service.addCardsToCollectionIfMissing([], cards, cards2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(cards);
          expect(expectedResult).toContain(cards2);
        });

        it('should accept null and undefined values', () => {
          const cards: ICards = { id: 123 };
          expectedResult = service.addCardsToCollectionIfMissing([], null, cards, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(cards);
        });

        it('should return initial array if no Cards is added', () => {
          const cardsCollection: ICards[] = [{ id: 123 }];
          expectedResult = service.addCardsToCollectionIfMissing(cardsCollection, undefined, null);
          expect(expectedResult).toEqual(cardsCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
