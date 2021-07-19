import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WishlistDetailComponent } from './wishlist-detail.component';

describe('Component Tests', () => {
  describe('Wishlist Management Detail Component', () => {
    let comp: WishlistDetailComponent;
    let fixture: ComponentFixture<WishlistDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [WishlistDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ wishlist: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(WishlistDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(WishlistDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load wishlist on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.wishlist).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
