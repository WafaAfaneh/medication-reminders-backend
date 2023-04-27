import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { MedicationFormService, MedicationFormGroup } from './medication-form.service';
import { IMedication } from '../medication.model';
import { MedicationService } from '../service/medication.service';
import { INotification } from 'app/entities/notification/notification.model';
import { NotificationService } from 'app/entities/notification/service/notification.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { MedicationType } from 'app/entities/enumerations/medication-type.model';

@Component({
  selector: 'jhi-medication-update',
  templateUrl: './medication-update.component.html',
})
export class MedicationUpdateComponent implements OnInit {
  isSaving = false;
  medication: IMedication | null = null;
  medicationTypeValues = Object.keys(MedicationType);

  notificationsCollection: INotification[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: MedicationFormGroup = this.medicationFormService.createMedicationFormGroup();

  constructor(
    protected medicationService: MedicationService,
    protected medicationFormService: MedicationFormService,
    protected notificationService: NotificationService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareNotification = (o1: INotification | null, o2: INotification | null): boolean =>
    this.notificationService.compareNotification(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ medication }) => {
      this.medication = medication;
      if (medication) {
        this.updateForm(medication);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const medication = this.medicationFormService.getMedication(this.editForm);
    if (medication.id !== null) {
      this.subscribeToSaveResponse(this.medicationService.update(medication));
    } else {
      this.subscribeToSaveResponse(this.medicationService.create(medication));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMedication>>): void {
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

  protected updateForm(medication: IMedication): void {
    this.medication = medication;
    this.medicationFormService.resetForm(this.editForm, medication);

    this.notificationsCollection = this.notificationService.addNotificationToCollectionIfMissing<INotification>(
      this.notificationsCollection,
      medication.notification
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, medication.user);
  }

  protected loadRelationshipsOptions(): void {
    this.notificationService
      .query({ 'medicationId.specified': 'false' })
      .pipe(map((res: HttpResponse<INotification[]>) => res.body ?? []))
      .pipe(
        map((notifications: INotification[]) =>
          this.notificationService.addNotificationToCollectionIfMissing<INotification>(notifications, this.medication?.notification)
        )
      )
      .subscribe((notifications: INotification[]) => (this.notificationsCollection = notifications));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.medication?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
