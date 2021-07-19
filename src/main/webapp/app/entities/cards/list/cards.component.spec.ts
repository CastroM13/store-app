jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CardsService } from '../service/cards.service';

import { CardsComponent } from './cards.component';

describe('Component Tests', () => {
  describe('Cards Management Component', () => {
    let comp: CardsComponent;
    let fixture: ComponentFixture<CardsComponent>;
    let service: CardsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CardsComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(CardsComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CardsComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(CardsService);

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
      expect(comp.cards?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
