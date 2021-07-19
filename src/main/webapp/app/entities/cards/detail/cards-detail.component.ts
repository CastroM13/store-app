import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICards } from '../cards.model';

@Component({
  selector: 'jhi-cards-detail',
  templateUrl: './cards-detail.component.html',
})
export class CardsDetailComponent implements OnInit {
  cards: ICards | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cards }) => {
      this.cards = cards;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
