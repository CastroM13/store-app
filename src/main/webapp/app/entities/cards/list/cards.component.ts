import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICards } from '../cards.model';
import { CardsService } from '../service/cards.service';
import { CardsDeleteDialogComponent } from '../delete/cards-delete-dialog.component';

@Component({
  selector: 'jhi-cards',
  templateUrl: './cards.component.html',
})
export class CardsComponent implements OnInit {
  cards?: ICards[];
  isLoading = false;
  currentSearch: string;

  constructor(protected cardsService: CardsService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.cardsService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<ICards[]>) => {
            this.isLoading = false;
            this.cards = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.cardsService.query().subscribe(
      (res: HttpResponse<ICards[]>) => {
        this.isLoading = false;
        this.cards = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ICards): number {
    return item.id!;
  }

  delete(cards: ICards): void {
    const modalRef = this.modalService.open(CardsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cards = cards;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
