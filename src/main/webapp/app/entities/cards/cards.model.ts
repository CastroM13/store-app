import { IUsers } from 'app/entities/users/users.model';

export interface ICards {
  id?: number;
  idCards?: number | null;
  identityDocument?: string | null;
  cardNumber?: string | null;
  securityCode?: number | null;
  expirationDate?: string | null;
  cardholder?: string | null;
  parcelQuantity?: number | null;
  idCards?: IUsers | null;
}

export class Cards implements ICards {
  constructor(
    public id?: number,
    public idCards?: number | null,
    public identityDocument?: string | null,
    public cardNumber?: string | null,
    public securityCode?: number | null,
    public expirationDate?: string | null,
    public cardholder?: string | null,
    public parcelQuantity?: number | null,
    public idCards?: IUsers | null
  ) {}
}

export function getCardsIdentifier(cards: ICards): number | undefined {
  return cards.id;
}
