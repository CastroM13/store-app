import { IUsers } from 'app/entities/users/users.model';

export interface IAddresses {
  id?: number;
  idAdresses?: number | null;
  country?: string | null;
  streetAddress?: string | null;
  postalCode?: string | null;
  city?: string | null;
  stateProvince?: string | null;
  idAdresses?: IUsers | null;
}

export class Addresses implements IAddresses {
  constructor(
    public id?: number,
    public idAdresses?: number | null,
    public country?: string | null,
    public streetAddress?: string | null,
    public postalCode?: string | null,
    public city?: string | null,
    public stateProvince?: string | null,
    public idAdresses?: IUsers | null
  ) {}
}

export function getAddressesIdentifier(addresses: IAddresses): number | undefined {
  return addresses.id;
}
