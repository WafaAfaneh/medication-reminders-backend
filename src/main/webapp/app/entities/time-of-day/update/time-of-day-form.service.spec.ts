import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../time-of-day.test-samples';

import { TimeOfDayFormService } from './time-of-day-form.service';

describe('TimeOfDay Form Service', () => {
  let service: TimeOfDayFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TimeOfDayFormService);
  });

  describe('Service methods', () => {
    describe('createTimeOfDayFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTimeOfDayFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            time: expect.any(Object),
            notification: expect.any(Object),
          })
        );
      });

      it('passing ITimeOfDay should create a new form with FormGroup', () => {
        const formGroup = service.createTimeOfDayFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            time: expect.any(Object),
            notification: expect.any(Object),
          })
        );
      });
    });

    describe('getTimeOfDay', () => {
      it('should return NewTimeOfDay for default TimeOfDay initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTimeOfDayFormGroup(sampleWithNewData);

        const timeOfDay = service.getTimeOfDay(formGroup) as any;

        expect(timeOfDay).toMatchObject(sampleWithNewData);
      });

      it('should return NewTimeOfDay for empty TimeOfDay initial value', () => {
        const formGroup = service.createTimeOfDayFormGroup();

        const timeOfDay = service.getTimeOfDay(formGroup) as any;

        expect(timeOfDay).toMatchObject({});
      });

      it('should return ITimeOfDay', () => {
        const formGroup = service.createTimeOfDayFormGroup(sampleWithRequiredData);

        const timeOfDay = service.getTimeOfDay(formGroup) as any;

        expect(timeOfDay).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITimeOfDay should not enable id FormControl', () => {
        const formGroup = service.createTimeOfDayFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTimeOfDay should disable id FormControl', () => {
        const formGroup = service.createTimeOfDayFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
