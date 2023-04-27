import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TimeOfDayFormService, TimeOfDayFormGroup } from './time-of-day-form.service';
import { ITimeOfDay } from '../time-of-day.model';
import { TimeOfDayService } from '../service/time-of-day.service';
import { INotification } from 'app/entities/notification/notification.model';
import { NotificationService } from 'app/entities/notification/service/notification.service';

@Component({
  selector: 'jhi-time-of-day-update',
  templateUrl: './time-of-day-update.component.html',
})
export class TimeOfDayUpdateComponent implements OnInit {
  isSaving = false;
  timeOfDay: ITimeOfDay | null = null;

  notificationsSharedCollection: INotification[] = [];

  editForm: TimeOfDayFormGroup = this.timeOfDayFormService.createTimeOfDayFormGroup();

  constructor(
    protected timeOfDayService: TimeOfDayService,
    protected timeOfDayFormService: TimeOfDayFormService,
    protected notificationService: NotificationService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareNotification = (o1: INotification | null, o2: INotification | null): boolean =>
    this.notificationService.compareNotification(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timeOfDay }) => {
      this.timeOfDay = timeOfDay;
      if (timeOfDay) {
        this.updateForm(timeOfDay);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const timeOfDay = this.timeOfDayFormService.getTimeOfDay(this.editForm);
    if (timeOfDay.id !== null) {
      this.subscribeToSaveResponse(this.timeOfDayService.update(timeOfDay));
    } else {
      this.subscribeToSaveResponse(this.timeOfDayService.create(timeOfDay));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITimeOfDay>>): void {
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

  protected updateForm(timeOfDay: ITimeOfDay): void {
    this.timeOfDay = timeOfDay;
    this.timeOfDayFormService.resetForm(this.editForm, timeOfDay);

    this.notificationsSharedCollection = this.notificationService.addNotificationToCollectionIfMissing<INotification>(
      this.notificationsSharedCollection,
      timeOfDay.notification
    );
  }

  protected loadRelationshipsOptions(): void {
    this.notificationService
      .query()
      .pipe(map((res: HttpResponse<INotification[]>) => res.body ?? []))
      .pipe(
        map((notifications: INotification[]) =>
          this.notificationService.addNotificationToCollectionIfMissing<INotification>(notifications, this.timeOfDay?.notification)
        )
      )
      .subscribe((notifications: INotification[]) => (this.notificationsSharedCollection = notifications));
  }
}
