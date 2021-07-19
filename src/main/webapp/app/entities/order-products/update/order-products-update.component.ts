import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IOrderProducts, OrderProducts } from '../order-products.model';
import { OrderProductsService } from '../service/order-products.service';
import { IOrders } from 'app/entities/orders/orders.model';
import { OrdersService } from 'app/entities/orders/service/orders.service';
import { IProducts } from 'app/entities/products/products.model';
import { ProductsService } from 'app/entities/products/service/products.service';

@Component({
  selector: 'jhi-order-products-update',
  templateUrl: './order-products-update.component.html',
})
export class OrderProductsUpdateComponent implements OnInit {
  isSaving = false;

  ordersSharedCollection: IOrders[] = [];
  productsSharedCollection: IProducts[] = [];

  editForm = this.fb.group({
    id: [],
    idOrderProducts: [],
    idOrder: [],
    idProduct: [],
    orders: [],
    products: [],
  });

  constructor(
    protected orderProductsService: OrderProductsService,
    protected ordersService: OrdersService,
    protected productsService: ProductsService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderProducts }) => {
      this.updateForm(orderProducts);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orderProducts = this.createFromForm();
    if (orderProducts.id !== undefined) {
      this.subscribeToSaveResponse(this.orderProductsService.update(orderProducts));
    } else {
      this.subscribeToSaveResponse(this.orderProductsService.create(orderProducts));
    }
  }

  trackOrdersById(index: number, item: IOrders): number {
    return item.id!;
  }

  trackProductsById(index: number, item: IProducts): number {
    return item.id!;
  }

  getSelectedOrders(option: IOrders, selectedVals?: IOrders[]): IOrders {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  getSelectedProducts(option: IProducts, selectedVals?: IProducts[]): IProducts {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrderProducts>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(orderProducts: IOrderProducts): void {
    this.editForm.patchValue({
      id: orderProducts.id,
      idOrderProducts: orderProducts.idOrderProducts,
      idOrder: orderProducts.idOrder,
      idProduct: orderProducts.idProduct,
      orders: orderProducts.orders,
      products: orderProducts.products,
    });

    this.ordersSharedCollection = this.ordersService.addOrdersToCollectionIfMissing(
      this.ordersSharedCollection,
      ...(orderProducts.orders ?? [])
    );
    this.productsSharedCollection = this.productsService.addProductsToCollectionIfMissing(
      this.productsSharedCollection,
      ...(orderProducts.products ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.ordersService
      .query()
      .pipe(map((res: HttpResponse<IOrders[]>) => res.body ?? []))
      .pipe(
        map((orders: IOrders[]) => this.ordersService.addOrdersToCollectionIfMissing(orders, ...(this.editForm.get('orders')!.value ?? [])))
      )
      .subscribe((orders: IOrders[]) => (this.ordersSharedCollection = orders));

    this.productsService
      .query()
      .pipe(map((res: HttpResponse<IProducts[]>) => res.body ?? []))
      .pipe(
        map((products: IProducts[]) =>
          this.productsService.addProductsToCollectionIfMissing(products, ...(this.editForm.get('products')!.value ?? []))
        )
      )
      .subscribe((products: IProducts[]) => (this.productsSharedCollection = products));
  }

  protected createFromForm(): IOrderProducts {
    return {
      ...new OrderProducts(),
      id: this.editForm.get(['id'])!.value,
      idOrderProducts: this.editForm.get(['idOrderProducts'])!.value,
      idOrder: this.editForm.get(['idOrder'])!.value,
      idProduct: this.editForm.get(['idProduct'])!.value,
      orders: this.editForm.get(['orders'])!.value,
      products: this.editForm.get(['products'])!.value,
    };
  }
}
