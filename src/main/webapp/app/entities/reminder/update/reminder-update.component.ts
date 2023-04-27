import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ReminderFormService, ReminderFormGroup } from './reminder-form.service';
import { IReminder } from '../reminder.model';
import { ReminderService } from '../service/reminder.service';
import { INotification } from 'app/entities/notification/notification.model';
import { NotificationService } from 'app/entities/notification/service/notification.service';
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  selector: 'jhi-reminder-update',
  templateUrl: './reminder-update.component.html',
})
export class ReminderUpdateComponent implements OnInit {
  isSaving = false;
  reminder: IReminder | null = null;
  statusValues = Object.keys(Status);

  notificationsSharedCollection: INotification[] = [];

  editForm: ReminderFormGroup = this.reminderFormService.createReminderFormGroup();

  constructor(
    protected reminderService: ReminderService,
    protected reminderFormService: ReminderFormService,
    protected notificationService: NotificationService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareNotification = (o1: INotification | null, o2: INotification | null): boolean =>
    this.notificationService.compareNotification(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reminder }) => {
      this.reminder = reminder;
      if (reminder) {
        this.updateForm(reminder);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reminder = this.reminderFormService.getReminder(this.editForm);
    if (reminder.id !== null) {
      this.subscribeToSaveResponse(this.reminderService.update(reminder));
    } else {
      this.subscribeToSaveResponse(this.reminderService.create(reminder));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReminder>>): void {
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

  protected updateForm(reminder: IReminder): void {
    this.reminder = reminder;
    this.reminderFormService.resetForm(this.editForm, reminder);

    this.notificationsSharedCollection = this.notificationService.addNotificationToCollectionIfMissing<INotification>(
      this.notificationsSharedCollection,
      reminder.notification
    );
  }

  protected loadRelationshipsOptions(): void {
    this.notificationService
      .query()
      .pipe(map((res: HttpResponse<INotification[]>) => res.body ?? []))
      .pipe(
        map((notifications: INotification[]) =>
          this.notificationService.addNotificationToCollectionIfMissing<INotification>(notifications, this.reminder?.notification)
        )
      )
      .subscribe((notifications: INotification[]) => (this.notificationsSharedCollection = notifications));
  }
}
