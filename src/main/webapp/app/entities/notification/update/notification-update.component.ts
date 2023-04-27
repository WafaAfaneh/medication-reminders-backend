import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { NotificationFormService, NotificationFormGroup } from './notification-form.service';
import { INotification } from '../notification.model';
import { NotificationService } from '../service/notification.service';
import { IFrequency } from 'app/entities/frequency/frequency.model';
import { FrequencyService } from 'app/entities/frequency/service/frequency.service';

@Component({
  selector: 'jhi-notification-update',
  templateUrl: './notification-update.component.html',
})
export class NotificationUpdateComponent implements OnInit {
  isSaving = false;
  notification: INotification | null = null;

  frequenciesCollection: IFrequency[] = [];

  editForm: NotificationFormGroup = this.notificationFormService.createNotificationFormGroup();

  constructor(
    protected notificationService: NotificationService,
    protected notificationFormService: NotificationFormService,
    protected frequencyService: FrequencyService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareFrequency = (o1: IFrequency | null, o2: IFrequency | null): boolean => this.frequencyService.compareFrequency(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notification }) => {
      this.notification = notification;
      if (notification) {
        this.updateForm(notification);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notification = this.notificationFormService.getNotification(this.editForm);
    if (notification.id !== null) {
      this.subscribeToSaveResponse(this.notificationService.update(notification));
    } else {
      this.subscribeToSaveResponse(this.notificationService.create(notification));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotification>>): void {
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

  protected updateForm(notification: INotification): void {
    this.notification = notification;
    this.notificationFormService.resetForm(this.editForm, notification);

    this.frequenciesCollection = this.frequencyService.addFrequencyToCollectionIfMissing<IFrequency>(
      this.frequenciesCollection,
      notification.frequency
    );
  }

  protected loadRelationshipsOptions(): void {
    this.frequencyService
      .query({ filter: 'notification-is-null' })
      .pipe(map((res: HttpResponse<IFrequency[]>) => res.body ?? []))
      .pipe(
        map((frequencies: IFrequency[]) =>
          this.frequencyService.addFrequencyToCollectionIfMissing<IFrequency>(frequencies, this.notification?.frequency)
        )
      )
      .subscribe((frequencies: IFrequency[]) => (this.frequenciesCollection = frequencies));
  }
}
