import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ReminderFormService } from './reminder-form.service';
import { ReminderService } from '../service/reminder.service';
import { IReminder } from '../reminder.model';
import { INotification } from 'app/entities/notification/notification.model';
import { NotificationService } from 'app/entities/notification/service/notification.service';

import { ReminderUpdateComponent } from './reminder-update.component';

describe('Reminder Management Update Component', () => {
  let comp: ReminderUpdateComponent;
  let fixture: ComponentFixture<ReminderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reminderFormService: ReminderFormService;
  let reminderService: ReminderService;
  let notificationService: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ReminderUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ReminderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReminderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reminderFormService = TestBed.inject(ReminderFormService);
    reminderService = TestBed.inject(ReminderService);
    notificationService = TestBed.inject(NotificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Notification query and add missing value', () => {
      const reminder: IReminder = { id: 456 };
      const notification: INotification = { id: 84883 };
      reminder.notification = notification;

      const notificationCollection: INotification[] = [{ id: 79222 }];
      jest.spyOn(notificationService, 'query').mockReturnValue(of(new HttpResponse({ body: notificationCollection })));
      const additionalNotifications = [notification];
      const expectedCollection: INotification[] = [...additionalNotifications, ...notificationCollection];
      jest.spyOn(notificationService, 'addNotificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reminder });
      comp.ngOnInit();

      expect(notificationService.query).toHaveBeenCalled();
      expect(notificationService.addNotificationToCollectionIfMissing).toHaveBeenCalledWith(
        notificationCollection,
        ...additionalNotifications.map(expect.objectContaining)
      );
      expect(comp.notificationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const reminder: IReminder = { id: 456 };
      const notification: INotification = { id: 51612 };
      reminder.notification = notification;

      activatedRoute.data = of({ reminder });
      comp.ngOnInit();

      expect(comp.notificationsSharedCollection).toContain(notification);
      expect(comp.reminder).toEqual(reminder);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReminder>>();
      const reminder = { id: 123 };
      jest.spyOn(reminderFormService, 'getReminder').mockReturnValue(reminder);
      jest.spyOn(reminderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reminder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reminder }));
      saveSubject.complete();

      // THEN
      expect(reminderFormService.getReminder).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reminderService.update).toHaveBeenCalledWith(expect.objectContaining(reminder));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReminder>>();
      const reminder = { id: 123 };
      jest.spyOn(reminderFormService, 'getReminder').mockReturnValue({ id: null });
      jest.spyOn(reminderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reminder: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reminder }));
      saveSubject.complete();

      // THEN
      expect(reminderFormService.getReminder).toHaveBeenCalled();
      expect(reminderService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReminder>>();
      const reminder = { id: 123 };
      jest.spyOn(reminderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reminder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reminderService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareNotification', () => {
      it('Should forward to notificationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(notificationService, 'compareNotification');
        comp.compareNotification(entity, entity2);
        expect(notificationService.compareNotification).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
