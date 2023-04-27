import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReminder, NewReminder } from '../reminder.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReminder for edit and NewReminderFormGroupInput for create.
 */
type ReminderFormGroupInput = IReminder | PartialWithRequiredKeyOf<NewReminder>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReminder | NewReminder> = Omit<T, 'date'> & {
  date?: string | null;
};

type ReminderFormRawValue = FormValueOf<IReminder>;

type NewReminderFormRawValue = FormValueOf<NewReminder>;

type ReminderFormDefaults = Pick<NewReminder, 'id' | 'date'>;

type ReminderFormGroupContent = {
  id: FormControl<ReminderFormRawValue['id'] | NewReminder['id']>;
  date: FormControl<ReminderFormRawValue['date']>;
  status: FormControl<ReminderFormRawValue['status']>;
  notification: FormControl<ReminderFormRawValue['notification']>;
};

export type ReminderFormGroup = FormGroup<ReminderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReminderFormService {
  createReminderFormGroup(reminder: ReminderFormGroupInput = { id: null }): ReminderFormGroup {
    const reminderRawValue = this.convertReminderToReminderRawValue({
      ...this.getFormDefaults(),
      ...reminder,
    });
    return new FormGroup<ReminderFormGroupContent>({
      id: new FormControl(
        { value: reminderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      date: new FormControl(reminderRawValue.date),
      status: new FormControl(reminderRawValue.status),
      notification: new FormControl(reminderRawValue.notification),
    });
  }

  getReminder(form: ReminderFormGroup): IReminder | NewReminder {
    return this.convertReminderRawValueToReminder(form.getRawValue() as ReminderFormRawValue | NewReminderFormRawValue);
  }

  resetForm(form: ReminderFormGroup, reminder: ReminderFormGroupInput): void {
    const reminderRawValue = this.convertReminderToReminderRawValue({ ...this.getFormDefaults(), ...reminder });
    form.reset(
      {
        ...reminderRawValue,
        id: { value: reminderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ReminderFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertReminderRawValueToReminder(rawReminder: ReminderFormRawValue | NewReminderFormRawValue): IReminder | NewReminder {
    return {
      ...rawReminder,
      date: dayjs(rawReminder.date, DATE_TIME_FORMAT),
    };
  }

  private convertReminderToReminderRawValue(
    reminder: IReminder | (Partial<NewReminder> & ReminderFormDefaults)
  ): ReminderFormRawValue | PartialWithRequiredKeyOf<NewReminderFormRawValue> {
    return {
      ...reminder,
      date: reminder.date ? reminder.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
