import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFrequency } from '../frequency.model';

@Component({
  selector: 'jhi-frequency-detail',
  templateUrl: './frequency-detail.component.html',
})
export class FrequencyDetailComponent implements OnInit {
  frequency: IFrequency | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ frequency }) => {
      this.frequency = frequency;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
