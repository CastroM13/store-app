import { IUsers } from 'app/entities/users/users.model';
import { IOrderProducts } from 'app/entities/order-products/order-products.model';

export interface IOrders {
  id?: number;
  idOrder?: number | null;
  idOrderProducts?: number | null;
  total?: number | null;
  idOrders?: IUsers | null;
  idOrderProducts?: IOrderProducts[] | null;
}

export class Orders implements IOrders {
  constructor(
    public id?: number,
    public idOrder?: number | null,
    public idOrderProducts?: number | null,
    public total?: number | null,
    public idOrders?: IUsers | null,
    public idOrderProducts?: IOrderProducts[] | null
  ) {}
}

export function getOrdersIdentifier(orders: IOrders): number | undefined {
  return orders.id;
}
