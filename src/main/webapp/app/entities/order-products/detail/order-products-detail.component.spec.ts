import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrderProductsDetailComponent } from './order-products-detail.component';

describe('Component Tests', () => {
  describe('OrderProducts Management Detail Component', () => {
    let comp: OrderProductsDetailComponent;
    let fixture: ComponentFixture<OrderProductsDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [OrderProductsDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ orderProducts: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(OrderProductsDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(OrderProductsDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load orderProducts on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.orderProducts).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
