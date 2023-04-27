import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMedication } from '../medication.model';

@Component({
  selector: 'jhi-medication-detail',
  templateUrl: './medication-detail.component.html',
})
export class MedicationDetailComponent implements OnInit {
  medication: IMedication | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ medication }) => {
      this.medication = medication;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
