import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../frequency.test-samples';

import { FrequencyFormService } from './frequency-form.service';

describe('Frequency Form Service', () => {
  let service: FrequencyFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FrequencyFormService);
  });

  describe('Service methods', () => {
    describe('createFrequencyFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFrequencyFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            saturday: expect.any(Object),
            sunday: expect.any(Object),
            monday: expect.any(Object),
            tuesday: expect.any(Object),
            wednesday: expect.any(Object),
            thursday: expect.any(Object),
            friday: expect.any(Object),
          })
        );
      });

      it('passing IFrequency should create a new form with FormGroup', () => {
        const formGroup = service.createFrequencyFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            saturday: expect.any(Object),
            sunday: expect.any(Object),
            monday: expect.any(Object),
            tuesday: expect.any(Object),
            wednesday: expect.any(Object),
            thursday: expect.any(Object),
            friday: expect.any(Object),
          })
        );
      });
    });

    describe('getFrequency', () => {
      it('should return NewFrequency for default Frequency initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFrequencyFormGroup(sampleWithNewData);

        const frequency = service.getFrequency(formGroup) as any;

        expect(frequency).toMatchObject(sampleWithNewData);
      });

      it('should return NewFrequency for empty Frequency initial value', () => {
        const formGroup = service.createFrequencyFormGroup();

        const frequency = service.getFrequency(formGroup) as any;

        expect(frequency).toMatchObject({});
      });

      it('should return IFrequency', () => {
        const formGroup = service.createFrequencyFormGroup(sampleWithRequiredData);

        const frequency = service.getFrequency(formGroup) as any;

        expect(frequency).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFrequency should not enable id FormControl', () => {
        const formGroup = service.createFrequencyFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFrequency should disable id FormControl', () => {
        const formGroup = service.createFrequencyFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
