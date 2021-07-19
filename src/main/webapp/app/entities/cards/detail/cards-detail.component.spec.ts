import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CardsDetailComponent } from './cards-detail.component';

describe('Component Tests', () => {
  describe('Cards Management Detail Component', () => {
    let comp: CardsDetailComponent;
    let fixture: ComponentFixture<CardsDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CardsDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ cards: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(CardsDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CardsDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load cards on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.cards).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
