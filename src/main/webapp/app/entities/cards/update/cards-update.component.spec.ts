jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CardsService } from '../service/cards.service';
import { ICards, Cards } from '../cards.model';
import { IUsers } from 'app/entities/users/users.model';
import { UsersService } from 'app/entities/users/service/users.service';

import { CardsUpdateComponent } from './cards-update.component';

describe('Component Tests', () => {
  describe('Cards Management Update Component', () => {
    let comp: CardsUpdateComponent;
    let fixture: ComponentFixture<CardsUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let cardsService: CardsService;
    let usersService: UsersService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CardsUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CardsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CardsUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      cardsService = TestBed.inject(CardsService);
      usersService = TestBed.inject(UsersService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Users query and add missing value', () => {
        const cards: ICards = { id: 456 };
        const idCards: IUsers = { id: 1401 };
        cards.idCards = idCards;

        const usersCollection: IUsers[] = [{ id: 80595 }];
        jest.spyOn(usersService, 'query').mockReturnValue(of(new HttpResponse({ body: usersCollection })));
        const additionalUsers = [idCards];
        const expectedCollection: IUsers[] = [...additionalUsers, ...usersCollection];
        jest.spyOn(usersService, 'addUsersToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ cards });
        comp.ngOnInit();

        expect(usersService.query).toHaveBeenCalled();
        expect(usersService.addUsersToCollectionIfMissing).toHaveBeenCalledWith(usersCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const cards: ICards = { id: 456 };
        const idCards: IUsers = { id: 13950 };
        cards.idCards = idCards;

        activatedRoute.data = of({ cards });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(cards));
        expect(comp.usersSharedCollection).toContain(idCards);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Cards>>();
        const cards = { id: 123 };
        jest.spyOn(cardsService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ cards });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: cards }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(cardsService.update).toHaveBeenCalledWith(cards);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Cards>>();
        const cards = new Cards();
        jest.spyOn(cardsService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ cards });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: cards }));
        saveSubject.complete();

        // THEN
        expect(cardsService.create).toHaveBeenCalledWith(cards);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Cards>>();
        const cards = { id: 123 };
        jest.spyOn(cardsService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ cards });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(cardsService.update).toHaveBeenCalledWith(cards);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUsersById', () => {
        it('Should return tracked Users primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUsersById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
