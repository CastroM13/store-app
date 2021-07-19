import { IOrders } from 'app/entities/orders/orders.model';
import { IProducts } from 'app/entities/products/products.model';

export interface IOrderProducts {
  id?: number;
  idOrderProducts?: number | null;
  idOrder?: number | null;
  idProduct?: number | null;
  orders?: IOrders[] | null;
  products?: IProducts[] | null;
}

export class OrderProducts implements IOrderProducts {
  constructor(
    public id?: number,
    public idOrderProducts?: number | null,
    public idOrder?: number | null,
    public idProduct?: number | null,
    public orders?: IOrders[] | null,
    public products?: IProducts[] | null
  ) {}
}

export function getOrderProductsIdentifier(orderProducts: IOrderProducts): number | undefined {
  return orderProducts.id;
}
