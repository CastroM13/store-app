import { IUsers } from 'app/entities/users/users.model';

export interface IWishlist {
  id?: number;
  idWishlist?: number | null;
  idUser?: number | null;
  idProducts?: number | null;
  idWishlist?: IUsers | null;
}

export class Wishlist implements IWishlist {
  constructor(
    public id?: number,
    public idWishlist?: number | null,
    public idUser?: number | null,
    public idProducts?: number | null,
    public idWishlist?: IUsers | null
  ) {}
}

export function getWishlistIdentifier(wishlist: IWishlist): number | undefined {
  return wishlist.id;
}
