import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IAddresses, getAddressesIdentifier } from '../addresses.model';

export type EntityResponseType = HttpResponse<IAddresses>;
export type EntityArrayResponseType = HttpResponse<IAddresses[]>;

@Injectable({ providedIn: 'root' })
export class AddressesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/addresses');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/addresses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(addresses: IAddresses): Observable<EntityResponseType> {
    return this.http.post<IAddresses>(this.resourceUrl, addresses, { observe: 'response' });
  }

  update(addresses: IAddresses): Observable<EntityResponseType> {
    return this.http.put<IAddresses>(`${this.resourceUrl}/${getAddressesIdentifier(addresses) as number}`, addresses, {
      observe: 'response',
    });
  }

  partialUpdate(addresses: IAddresses): Observable<EntityResponseType> {
    return this.http.patch<IAddresses>(`${this.resourceUrl}/${getAddressesIdentifier(addresses) as number}`, addresses, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAddresses>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAddresses[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAddresses[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addAddressesToCollectionIfMissing(
    addressesCollection: IAddresses[],
    ...addressesToCheck: (IAddresses | null | undefined)[]
  ): IAddresses[] {
    const addresses: IAddresses[] = addressesToCheck.filter(isPresent);
    if (addresses.length > 0) {
      const addressesCollectionIdentifiers = addressesCollection.map(addressesItem => getAddressesIdentifier(addressesItem)!);
      const addressesToAdd = addresses.filter(addressesItem => {
        const addressesIdentifier = getAddressesIdentifier(addressesItem);
        if (addressesIdentifier == null || addressesCollectionIdentifiers.includes(addressesIdentifier)) {
          return false;
        }
        addressesCollectionIdentifiers.push(addressesIdentifier);
        return true;
      });
      return [...addressesToAdd, ...addressesCollection];
    }
    return addressesCollection;
  }
}
