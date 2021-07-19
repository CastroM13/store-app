import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IOrders, getOrdersIdentifier } from '../orders.model';

export type EntityResponseType = HttpResponse<IOrders>;
export type EntityArrayResponseType = HttpResponse<IOrders[]>;

@Injectable({ providedIn: 'root' })
export class OrdersService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/orders');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/orders');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orders: IOrders): Observable<EntityResponseType> {
    return this.http.post<IOrders>(this.resourceUrl, orders, { observe: 'response' });
  }

  update(orders: IOrders): Observable<EntityResponseType> {
    return this.http.put<IOrders>(`${this.resourceUrl}/${getOrdersIdentifier(orders) as number}`, orders, { observe: 'response' });
  }

  partialUpdate(orders: IOrders): Observable<EntityResponseType> {
    return this.http.patch<IOrders>(`${this.resourceUrl}/${getOrdersIdentifier(orders) as number}`, orders, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOrders>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrders[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrders[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addOrdersToCollectionIfMissing(ordersCollection: IOrders[], ...ordersToCheck: (IOrders | null | undefined)[]): IOrders[] {
    const orders: IOrders[] = ordersToCheck.filter(isPresent);
    if (orders.length > 0) {
      const ordersCollectionIdentifiers = ordersCollection.map(ordersItem => getOrdersIdentifier(ordersItem)!);
      const ordersToAdd = orders.filter(ordersItem => {
        const ordersIdentifier = getOrdersIdentifier(ordersItem);
        if (ordersIdentifier == null || ordersCollectionIdentifiers.includes(ordersIdentifier)) {
          return false;
        }
        ordersCollectionIdentifiers.push(ordersIdentifier);
        return true;
      });
      return [...ordersToAdd, ...ordersCollection];
    }
    return ordersCollection;
  }
}
