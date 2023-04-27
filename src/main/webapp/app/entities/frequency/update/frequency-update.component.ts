import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { FrequencyFormService, FrequencyFormGroup } from './frequency-form.service';
import { IFrequency } from '../frequency.model';
import { FrequencyService } from '../service/frequency.service';
import { FrequencyType } from 'app/entities/enumerations/frequency-type.model';

@Component({
  selector: 'jhi-frequency-update',
  templateUrl: './frequency-update.component.html',
})
export class FrequencyUpdateComponent implements OnInit {
  isSaving = false;
  frequency: IFrequency | null = null;
  frequencyTypeValues = Object.keys(FrequencyType);

  editForm: FrequencyFormGroup = this.frequencyFormService.createFrequencyFormGroup();

  constructor(
    protected frequencyService: FrequencyService,
    protected frequencyFormService: FrequencyFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ frequency }) => {
      this.frequency = frequency;
      if (frequency) {
        this.updateForm(frequency);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const frequency = this.frequencyFormService.getFrequency(this.editForm);
    if (frequency.id !== null) {
      this.subscribeToSaveResponse(this.frequencyService.update(frequency));
    } else {
      this.subscribeToSaveResponse(this.frequencyService.create(frequency));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFrequency>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(frequency: IFrequency): void {
    this.frequency = frequency;
    this.frequencyFormService.resetForm(this.editForm, frequency);
  }
}
