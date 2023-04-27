import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TimeOfDayFormService } from './time-of-day-form.service';
import { TimeOfDayService } from '../service/time-of-day.service';
import { ITimeOfDay } from '../time-of-day.model';
import { INotification } from 'app/entities/notification/notification.model';
import { NotificationService } from 'app/entities/notification/service/notification.service';

import { TimeOfDayUpdateComponent } from './time-of-day-update.component';

describe('TimeOfDay Management Update Component', () => {
  let comp: TimeOfDayUpdateComponent;
  let fixture: ComponentFixture<TimeOfDayUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let timeOfDayFormService: TimeOfDayFormService;
  let timeOfDayService: TimeOfDayService;
  let notificationService: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TimeOfDayUpdateComponent],
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
      .overrideTemplate(TimeOfDayUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TimeOfDayUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    timeOfDayFormService = TestBed.inject(TimeOfDayFormService);
    timeOfDayService = TestBed.inject(TimeOfDayService);
    notificationService = TestBed.inject(NotificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Notification query and add missing value', () => {
      const timeOfDay: ITimeOfDay = { id: 456 };
      const notification: INotification = { id: 35039 };
      timeOfDay.notification = notification;

      const notificationCollection: INotification[] = [{ id: 98880 }];
      jest.spyOn(notificationService, 'query').mockReturnValue(of(new HttpResponse({ body: notificationCollection })));
      const additionalNotifications = [notification];
      const expectedCollection: INotification[] = [...additionalNotifications, ...notificationCollection];
      jest.spyOn(notificationService, 'addNotificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ timeOfDay });
      comp.ngOnInit();

      expect(notificationService.query).toHaveBeenCalled();
      expect(notificationService.addNotificationToCollectionIfMissing).toHaveBeenCalledWith(
        notificationCollection,
        ...additionalNotifications.map(expect.objectContaining)
      );
      expect(comp.notificationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const timeOfDay: ITimeOfDay = { id: 456 };
      const notification: INotification = { id: 1572 };
      timeOfDay.notification = notification;

      activatedRoute.data = of({ timeOfDay });
      comp.ngOnInit();

      expect(comp.notificationsSharedCollection).toContain(notification);
      expect(comp.timeOfDay).toEqual(timeOfDay);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITimeOfDay>>();
      const timeOfDay = { id: 123 };
      jest.spyOn(timeOfDayFormService, 'getTimeOfDay').mockReturnValue(timeOfDay);
      jest.spyOn(timeOfDayService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeOfDay });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: timeOfDay }));
      saveSubject.complete();

      // THEN
      expect(timeOfDayFormService.getTimeOfDay).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(timeOfDayService.update).toHaveBeenCalledWith(expect.objectContaining(timeOfDay));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITimeOfDay>>();
      const timeOfDay = { id: 123 };
      jest.spyOn(timeOfDayFormService, 'getTimeOfDay').mockReturnValue({ id: null });
      jest.spyOn(timeOfDayService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeOfDay: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: timeOfDay }));
      saveSubject.complete();

      // THEN
      expect(timeOfDayFormService.getTimeOfDay).toHaveBeenCalled();
      expect(timeOfDayService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITimeOfDay>>();
      const timeOfDay = { id: 123 };
      jest.spyOn(timeOfDayService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeOfDay });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(timeOfDayService.update).toHaveBeenCalled();
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
