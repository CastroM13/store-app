import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrderProducts } from '../order-products.model';

@Component({
  selector: 'jhi-order-products-detail',
  templateUrl: './order-products-detail.component.html',
})
export class OrderProductsDetailComponent implements OnInit {
  orderProducts: IOrderProducts | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderProducts }) => {
      this.orderProducts = orderProducts;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
