import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITimeOfDay } from '../time-of-day.model';

@Component({
  selector: 'jhi-time-of-day-detail',
  templateUrl: './time-of-day-detail.component.html',
})
export class TimeOfDayDetailComponent implements OnInit {
  timeOfDay: ITimeOfDay | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timeOfDay }) => {
      this.timeOfDay = timeOfDay;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
