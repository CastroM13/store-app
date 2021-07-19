import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAddresses, Addresses } from '../addresses.model';

import { AddressesService } from './addresses.service';

describe('Service Tests', () => {
  describe('Addresses Service', () => {
    let service: AddressesService;
    let httpMock: HttpTestingController;
    let elemDefault: IAddresses;
    let expectedResult: IAddresses | IAddresses[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(AddressesService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        idAdresses: 0,
        country: 'AAAAAAA',
        streetAddress: 'AAAAAAA',
        postalCode: 'AAAAAAA',
        city: 'AAAAAAA',
        stateProvince: 'AAAAAAA',
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

      it('should create a Addresses', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Addresses()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Addresses', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            idAdresses: 1,
            country: 'BBBBBB',
            streetAddress: 'BBBBBB',
            postalCode: 'BBBBBB',
            city: 'BBBBBB',
            stateProvince: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Addresses', () => {
        const patchObject = Object.assign(
          {
            streetAddress: 'BBBBBB',
            postalCode: 'BBBBBB',
          },
          new Addresses()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Addresses', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            idAdresses: 1,
            country: 'BBBBBB',
            streetAddress: 'BBBBBB',
            postalCode: 'BBBBBB',
            city: 'BBBBBB',
            stateProvince: 'BBBBBB',
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

      it('should delete a Addresses', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addAddressesToCollectionIfMissing', () => {
        it('should add a Addresses to an empty array', () => {
          const addresses: IAddresses = { id: 123 };
          expectedResult = service.addAddressesToCollectionIfMissing([], addresses);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(addresses);
        });

        it('should not add a Addresses to an array that contains it', () => {
          const addresses: IAddresses = { id: 123 };
          const addressesCollection: IAddresses[] = [
            {
              ...addresses,
            },
            { id: 456 },
          ];
          expectedResult = service.addAddressesToCollectionIfMissing(addressesCollection, addresses);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Addresses to an array that doesn't contain it", () => {
          const addresses: IAddresses = { id: 123 };
          const addressesCollection: IAddresses[] = [{ id: 456 }];
          expectedResult = service.addAddressesToCollectionIfMissing(addressesCollection, addresses);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(addresses);
        });

        it('should add only unique Addresses to an array', () => {
          const addressesArray: IAddresses[] = [{ id: 123 }, { id: 456 }, { id: 27315 }];
          const addressesCollection: IAddresses[] = [{ id: 123 }];
          expectedResult = service.addAddressesToCollectionIfMissing(addressesCollection, ...addressesArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const addresses: IAddresses = { id: 123 };
          const addresses2: IAddresses = { id: 456 };
          expectedResult = service.addAddressesToCollectionIfMissing([], addresses, addresses2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(addresses);
          expect(expectedResult).toContain(addresses2);
        });

        it('should accept null and undefined values', () => {
          const addresses: IAddresses = { id: 123 };
          expectedResult = service.addAddressesToCollectionIfMissing([], null, addresses, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(addresses);
        });

        it('should return initial array if no Addresses is added', () => {
          const addressesCollection: IAddresses[] = [{ id: 123 }];
          expectedResult = service.addAddressesToCollectionIfMissing(addressesCollection, undefined, null);
          expect(expectedResult).toEqual(addressesCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
