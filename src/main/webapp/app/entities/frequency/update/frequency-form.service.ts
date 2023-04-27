import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IFrequency, NewFrequency } from '../frequency.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFrequency for edit and NewFrequencyFormGroupInput for create.
 */
type FrequencyFormGroupInput = IFrequency | PartialWithRequiredKeyOf<NewFrequency>;

type FrequencyFormDefaults = Pick<NewFrequency, 'id' | 'saturday' | 'sunday' | 'monday' | 'tuesday' | 'wednesday' | 'thursday' | 'friday'>;

type FrequencyFormGroupContent = {
  id: FormControl<IFrequency['id'] | NewFrequency['id']>;
  type: FormControl<IFrequency['type']>;
  saturday: FormControl<IFrequency['saturday']>;
  sunday: FormControl<IFrequency['sunday']>;
  monday: FormControl<IFrequency['monday']>;
  tuesday: FormControl<IFrequency['tuesday']>;
  wednesday: FormControl<IFrequency['wednesday']>;
  thursday: FormControl<IFrequency['thursday']>;
  friday: FormControl<IFrequency['friday']>;
};

export type FrequencyFormGroup = FormGroup<FrequencyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FrequencyFormService {
  createFrequencyFormGroup(frequency: FrequencyFormGroupInput = { id: null }): FrequencyFormGroup {
    const frequencyRawValue = {
      ...this.getFormDefaults(),
      ...frequency,
    };
    return new FormGroup<FrequencyFormGroupContent>({
      id: new FormControl(
        { value: frequencyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      type: new FormControl(frequencyRawValue.type),
      saturday: new FormControl(frequencyRawValue.saturday),
      sunday: new FormControl(frequencyRawValue.sunday),
      monday: new FormControl(frequencyRawValue.monday),
      tuesday: new FormControl(frequencyRawValue.tuesday),
      wednesday: new FormControl(frequencyRawValue.wednesday),
      thursday: new FormControl(frequencyRawValue.thursday),
      friday: new FormControl(frequencyRawValue.friday),
    });
  }

  getFrequency(form: FrequencyFormGroup): IFrequency | NewFrequency {
    return form.getRawValue() as IFrequency | NewFrequency;
  }

  resetForm(form: FrequencyFormGroup, frequency: FrequencyFormGroupInput): void {
    const frequencyRawValue = { ...this.getFormDefaults(), ...frequency };
    form.reset(
      {
        ...frequencyRawValue,
        id: { value: frequencyRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FrequencyFormDefaults {
    return {
      id: null,
      saturday: false,
      sunday: false,
      monday: false,
      tuesday: false,
      wednesday: false,
      thursday: false,
      friday: false,
    };
  }
}
