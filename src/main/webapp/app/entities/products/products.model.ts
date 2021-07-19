import { IOrderProducts } from 'app/entities/order-products/order-products.model';

export interface IProducts {
  id?: number;
  idProducts?: number | null;
  name?: string | null;
  value?: number | null;
  image?: string | null;
  description?: string | null;
  idProducts?: IOrderProducts[] | null;
}

export class Products implements IProducts {
  constructor(
    public id?: number,
    public idProducts?: number | null,
    public name?: string | null,
    public value?: number | null,
    public image?: string | null,
    public description?: string | null,
    public idProducts?: IOrderProducts[] | null
  ) {}
}

export function getProductsIdentifier(products: IProducts): number | undefined {
  return products.id;
}
