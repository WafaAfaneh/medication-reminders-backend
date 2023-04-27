import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MedicationFormService } from './medication-form.service';
import { MedicationService } from '../service/medication.service';
import { IMedication } from '../medication.model';
import { INotification } from 'app/entities/notification/notification.model';
import { NotificationService } from 'app/entities/notification/service/notification.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { MedicationUpdateComponent } from './medication-update.component';

describe('Medication Management Update Component', () => {
  let comp: MedicationUpdateComponent;
  let fixture: ComponentFixture<MedicationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let medicationFormService: MedicationFormService;
  let medicationService: MedicationService;
  let notificationService: NotificationService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MedicationUpdateComponent],
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
      .overrideTemplate(MedicationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MedicationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    medicationFormService = TestBed.inject(MedicationFormService);
    medicationService = TestBed.inject(MedicationService);
    notificationService = TestBed.inject(NotificationService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call notification query and add missing value', () => {
      const medication: IMedication = { id: 456 };
      const notification: INotification = { id: 57663 };
      medication.notification = notification;

      const notificationCollection: INotification[] = [{ id: 26708 }];
      jest.spyOn(notificationService, 'query').mockReturnValue(of(new HttpResponse({ body: notificationCollection })));
      const expectedCollection: INotification[] = [notification, ...notificationCollection];
      jest.spyOn(notificationService, 'addNotificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ medication });
      comp.ngOnInit();

      expect(notificationService.query).toHaveBeenCalled();
      expect(notificationService.addNotificationToCollectionIfMissing).toHaveBeenCalledWith(notificationCollection, notification);
      expect(comp.notificationsCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const medication: IMedication = { id: 456 };
      const user: IUser = { id: 15394 };
      medication.user = user;

      const userCollection: IUser[] = [{ id: 52768 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ medication });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const medication: IMedication = { id: 456 };
      const notification: INotification = { id: 27068 };
      medication.notification = notification;
      const user: IUser = { id: 4192 };
      medication.user = user;

      activatedRoute.data = of({ medication });
      comp.ngOnInit();

      expect(comp.notificationsCollection).toContain(notification);
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.medication).toEqual(medication);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMedication>>();
      const medication = { id: 123 };
      jest.spyOn(medicationFormService, 'getMedication').mockReturnValue(medication);
      jest.spyOn(medicationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ medication });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: medication }));
      saveSubject.complete();

      // THEN
      expect(medicationFormService.getMedication).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(medicationService.update).toHaveBeenCalledWith(expect.objectContaining(medication));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMedication>>();
      const medication = { id: 123 };
      jest.spyOn(medicationFormService, 'getMedication').mockReturnValue({ id: null });
      jest.spyOn(medicationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ medication: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: medication }));
      saveSubject.complete();

      // THEN
      expect(medicationFormService.getMedication).toHaveBeenCalled();
      expect(medicationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMedication>>();
      const medication = { id: 123 };
      jest.spyOn(medicationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ medication });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(medicationService.update).toHaveBeenCalled();
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

    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
