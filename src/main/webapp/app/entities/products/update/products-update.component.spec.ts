jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ProductsService } from '../service/products.service';
import { IProducts, Products } from '../products.model';

import { ProductsUpdateComponent } from './products-update.component';

describe('Component Tests', () => {
  describe('Products Management Update Component', () => {
    let comp: ProductsUpdateComponent;
    let fixture: ComponentFixture<ProductsUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let productsService: ProductsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ProductsUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ProductsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ProductsUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      productsService = TestBed.inject(ProductsService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const products: IProducts = { id: 456 };

        activatedRoute.data = of({ products });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(products));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Products>>();
        const products = { id: 123 };
        jest.spyOn(productsService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ products });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: products }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(productsService.update).toHaveBeenCalledWith(products);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Products>>();
        const products = new Products();
        jest.spyOn(productsService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ products });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: products }));
        saveSubject.complete();

        // THEN
        expect(productsService.create).toHaveBeenCalledWith(products);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Products>>();
        const products = { id: 123 };
        jest.spyOn(productsService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ products });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(productsService.update).toHaveBeenCalledWith(products);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
