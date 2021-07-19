jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrderProductsService } from '../service/order-products.service';

import { OrderProductsComponent } from './order-products.component';

describe('Component Tests', () => {
  describe('OrderProducts Management Component', () => {
    let comp: OrderProductsComponent;
    let fixture: ComponentFixture<OrderProductsComponent>;
    let service: OrderProductsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [OrderProductsComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(OrderProductsComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OrderProductsComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(OrderProductsService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.orderProducts?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
