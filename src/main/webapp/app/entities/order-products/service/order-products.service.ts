import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IOrderProducts, getOrderProductsIdentifier } from '../order-products.model';

export type EntityResponseType = HttpResponse<IOrderProducts>;
export type EntityArrayResponseType = HttpResponse<IOrderProducts[]>;

@Injectable({ providedIn: 'root' })
export class OrderProductsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/order-products');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/order-products');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orderProducts: IOrderProducts): Observable<EntityResponseType> {
    return this.http.post<IOrderProducts>(this.resourceUrl, orderProducts, { observe: 'response' });
  }

  update(orderProducts: IOrderProducts): Observable<EntityResponseType> {
    return this.http.put<IOrderProducts>(`${this.resourceUrl}/${getOrderProductsIdentifier(orderProducts) as number}`, orderProducts, {
      observe: 'response',
    });
  }

  partialUpdate(orderProducts: IOrderProducts): Observable<EntityResponseType> {
    return this.http.patch<IOrderProducts>(`${this.resourceUrl}/${getOrderProductsIdentifier(orderProducts) as number}`, orderProducts, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOrderProducts>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrderProducts[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrderProducts[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addOrderProductsToCollectionIfMissing(
    orderProductsCollection: IOrderProducts[],
    ...orderProductsToCheck: (IOrderProducts | null | undefined)[]
  ): IOrderProducts[] {
    const orderProducts: IOrderProducts[] = orderProductsToCheck.filter(isPresent);
    if (orderProducts.length > 0) {
      const orderProductsCollectionIdentifiers = orderProductsCollection.map(
        orderProductsItem => getOrderProductsIdentifier(orderProductsItem)!
      );
      const orderProductsToAdd = orderProducts.filter(orderProductsItem => {
        const orderProductsIdentifier = getOrderProductsIdentifier(orderProductsItem);
        if (orderProductsIdentifier == null || orderProductsCollectionIdentifiers.includes(orderProductsIdentifier)) {
          return false;
        }
        orderProductsCollectionIdentifiers.push(orderProductsIdentifier);
        return true;
      });
      return [...orderProductsToAdd, ...orderProductsCollection];
    }
    return orderProductsCollection;
  }
}
