jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UsersService } from '../service/users.service';

import { UsersComponent } from './users.component';

describe('Component Tests', () => {
  describe('Users Management Component', () => {
    let comp: UsersComponent;
    let fixture: ComponentFixture<UsersComponent>;
    let service: UsersService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [UsersComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(UsersComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UsersComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(UsersService);

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
      expect(comp.users?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
