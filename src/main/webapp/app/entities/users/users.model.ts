import { IAddresses } from 'app/entities/addresses/addresses.model';
import { ICards } from 'app/entities/cards/cards.model';
import { IOrders } from 'app/entities/orders/orders.model';
import { IWishlist } from 'app/entities/wishlist/wishlist.model';

export interface IUsers {
  id?: number;
  idUser?: number | null;
  name?: string | null;
  email?: string | null;
  password?: string | null;
  idAdresses?: number | null;
  idCards?: number | null;
  idOrders?: number | null;
  idWishlist?: number | null;
  addresses?: IAddresses[] | null;
  cards?: ICards[] | null;
  orders?: IOrders[] | null;
  wishlists?: IWishlist[] | null;
}

export class Users implements IUsers {
  constructor(
    public id?: number,
    public idUser?: number | null,
    public name?: string | null,
    public email?: string | null,
    public password?: string | null,
    public idAdresses?: number | null,
    public idCards?: number | null,
    public idOrders?: number | null,
    public idWishlist?: number | null,
    public addresses?: IAddresses[] | null,
    public cards?: ICards[] | null,
    public orders?: IOrders[] | null,
    public wishlists?: IWishlist[] | null
  ) {}
}

export function getUsersIdentifier(users: IUsers): number | undefined {
  return users.id;
}
