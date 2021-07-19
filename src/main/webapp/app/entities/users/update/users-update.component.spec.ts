jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { UsersService } from '../service/users.service';
import { IUsers, Users } from '../users.model';

import { UsersUpdateComponent } from './users-update.component';

describe('Component Tests', () => {
  describe('Users Management Update Component', () => {
    let comp: UsersUpdateComponent;
    let fixture: ComponentFixture<UsersUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let usersService: UsersService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [UsersUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(UsersUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UsersUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      usersService = TestBed.inject(UsersService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const users: IUsers = { id: 456 };

        activatedRoute.data = of({ users });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(users));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Users>>();
        const users = { id: 123 };
        jest.spyOn(usersService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ users });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: users }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(usersService.update).toHaveBeenCalledWith(users);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Users>>();
        const users = new Users();
        jest.spyOn(usersService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ users });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: users }));
        saveSubject.complete();

        // THEN
        expect(usersService.create).toHaveBeenCalledWith(users);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Users>>();
        const users = { id: 123 };
        jest.spyOn(usersService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ users });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(usersService.update).toHaveBeenCalledWith(users);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
