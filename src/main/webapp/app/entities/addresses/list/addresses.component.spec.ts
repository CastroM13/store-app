jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AddressesService } from '../service/addresses.service';

import { AddressesComponent } from './addresses.component';

describe('Component Tests', () => {
  describe('Addresses Management Component', () => {
    let comp: AddressesComponent;
    let fixture: ComponentFixture<AddressesComponent>;
    let service: AddressesService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AddressesComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(AddressesComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AddressesComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(AddressesService);

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
      expect(comp.addresses?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
