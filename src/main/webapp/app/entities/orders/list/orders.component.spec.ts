jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrdersService } from '../service/orders.service';

import { OrdersComponent } from './orders.component';

describe('Component Tests', () => {
  describe('Orders Management Component', () => {
    let comp: OrdersComponent;
    let fixture: ComponentFixture<OrdersComponent>;
    let service: OrdersService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [OrdersComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(OrdersComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OrdersComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(OrdersService);

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
      expect(comp.orders?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
