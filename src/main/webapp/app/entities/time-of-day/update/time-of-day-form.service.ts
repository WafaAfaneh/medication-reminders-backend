import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITimeOfDay, NewTimeOfDay } from '../time-of-day.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITimeOfDay for edit and NewTimeOfDayFormGroupInput for create.
 */
type TimeOfDayFormGroupInput = ITimeOfDay | PartialWithRequiredKeyOf<NewTimeOfDay>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITimeOfDay | NewTimeOfDay> = Omit<T, 'time'> & {
  time?: string | null;
};

type TimeOfDayFormRawValue = FormValueOf<ITimeOfDay>;

type NewTimeOfDayFormRawValue = FormValueOf<NewTimeOfDay>;

type TimeOfDayFormDefaults = Pick<NewTimeOfDay, 'id' | 'time'>;

type TimeOfDayFormGroupContent = {
  id: FormControl<TimeOfDayFormRawValue['id'] | NewTimeOfDay['id']>;
  time: FormControl<TimeOfDayFormRawValue['time']>;
  notification: FormControl<TimeOfDayFormRawValue['notification']>;
};

export type TimeOfDayFormGroup = FormGroup<TimeOfDayFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TimeOfDayFormService {
  createTimeOfDayFormGroup(timeOfDay: TimeOfDayFormGroupInput = { id: null }): TimeOfDayFormGroup {
    const timeOfDayRawValue = this.convertTimeOfDayToTimeOfDayRawValue({
      ...this.getFormDefaults(),
      ...timeOfDay,
    });
    return new FormGroup<TimeOfDayFormGroupContent>({
      id: new FormControl(
        { value: timeOfDayRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      time: new FormControl(timeOfDayRawValue.time),
      notification: new FormControl(timeOfDayRawValue.notification),
    });
  }

  getTimeOfDay(form: TimeOfDayFormGroup): ITimeOfDay | NewTimeOfDay {
    return this.convertTimeOfDayRawValueToTimeOfDay(form.getRawValue() as TimeOfDayFormRawValue | NewTimeOfDayFormRawValue);
  }

  resetForm(form: TimeOfDayFormGroup, timeOfDay: TimeOfDayFormGroupInput): void {
    const timeOfDayRawValue = this.convertTimeOfDayToTimeOfDayRawValue({ ...this.getFormDefaults(), ...timeOfDay });
    form.reset(
      {
        ...timeOfDayRawValue,
        id: { value: timeOfDayRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TimeOfDayFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      time: currentTime,
    };
  }

  private convertTimeOfDayRawValueToTimeOfDay(rawTimeOfDay: TimeOfDayFormRawValue | NewTimeOfDayFormRawValue): ITimeOfDay | NewTimeOfDay {
    return {
      ...rawTimeOfDay,
      time: dayjs(rawTimeOfDay.time, DATE_TIME_FORMAT),
    };
  }

  private convertTimeOfDayToTimeOfDayRawValue(
    timeOfDay: ITimeOfDay | (Partial<NewTimeOfDay> & TimeOfDayFormDefaults)
  ): TimeOfDayFormRawValue | PartialWithRequiredKeyOf<NewTimeOfDayFormRawValue> {
    return {
      ...timeOfDay,
      time: timeOfDay.time ? timeOfDay.time.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
