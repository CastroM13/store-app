import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IWishlist, getWishlistIdentifier } from '../wishlist.model';

export type EntityResponseType = HttpResponse<IWishlist>;
export type EntityArrayResponseType = HttpResponse<IWishlist[]>;

@Injectable({ providedIn: 'root' })
export class WishlistService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/wishlists');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/wishlists');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(wishlist: IWishlist): Observable<EntityResponseType> {
    return this.http.post<IWishlist>(this.resourceUrl, wishlist, { observe: 'response' });
  }

  update(wishlist: IWishlist): Observable<EntityResponseType> {
    return this.http.put<IWishlist>(`${this.resourceUrl}/${getWishlistIdentifier(wishlist) as number}`, wishlist, { observe: 'response' });
  }

  partialUpdate(wishlist: IWishlist): Observable<EntityResponseType> {
    return this.http.patch<IWishlist>(`${this.resourceUrl}/${getWishlistIdentifier(wishlist) as number}`, wishlist, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IWishlist>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IWishlist[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IWishlist[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addWishlistToCollectionIfMissing(wishlistCollection: IWishlist[], ...wishlistsToCheck: (IWishlist | null | undefined)[]): IWishlist[] {
    const wishlists: IWishlist[] = wishlistsToCheck.filter(isPresent);
    if (wishlists.length > 0) {
      const wishlistCollectionIdentifiers = wishlistCollection.map(wishlistItem => getWishlistIdentifier(wishlistItem)!);
      const wishlistsToAdd = wishlists.filter(wishlistItem => {
        const wishlistIdentifier = getWishlistIdentifier(wishlistItem);
        if (wishlistIdentifier == null || wishlistCollectionIdentifiers.includes(wishlistIdentifier)) {
          return false;
        }
        wishlistCollectionIdentifiers.push(wishlistIdentifier);
        return true;
      });
      return [...wishlistsToAdd, ...wishlistCollection];
    }
    return wishlistCollection;
  }
}
