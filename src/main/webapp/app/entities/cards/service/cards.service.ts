import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICards, getCardsIdentifier } from '../cards.model';

export type EntityResponseType = HttpResponse<ICards>;
export type EntityArrayResponseType = HttpResponse<ICards[]>;

@Injectable({ providedIn: 'root' })
export class CardsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cards');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/cards');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cards: ICards): Observable<EntityResponseType> {
    return this.http.post<ICards>(this.resourceUrl, cards, { observe: 'response' });
  }

  update(cards: ICards): Observable<EntityResponseType> {
    return this.http.put<ICards>(`${this.resourceUrl}/${getCardsIdentifier(cards) as number}`, cards, { observe: 'response' });
  }

  partialUpdate(cards: ICards): Observable<EntityResponseType> {
    return this.http.patch<ICards>(`${this.resourceUrl}/${getCardsIdentifier(cards) as number}`, cards, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICards>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICards[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICards[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCardsToCollectionIfMissing(cardsCollection: ICards[], ...cardsToCheck: (ICards | null | undefined)[]): ICards[] {
    const cards: ICards[] = cardsToCheck.filter(isPresent);
    if (cards.length > 0) {
      const cardsCollectionIdentifiers = cardsCollection.map(cardsItem => getCardsIdentifier(cardsItem)!);
      const cardsToAdd = cards.filter(cardsItem => {
        const cardsIdentifier = getCardsIdentifier(cardsItem);
        if (cardsIdentifier == null || cardsCollectionIdentifiers.includes(cardsIdentifier)) {
          return false;
        }
        cardsCollectionIdentifiers.push(cardsIdentifier);
        return true;
      });
      return [...cardsToAdd, ...cardsCollection];
    }
    return cardsCollection;
  }
}
