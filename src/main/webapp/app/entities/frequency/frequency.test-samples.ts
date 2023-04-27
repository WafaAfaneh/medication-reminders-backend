import { FrequencyType } from 'app/entities/enumerations/frequency-type.model';

import { IFrequency, NewFrequency } from './frequency.model';

export const sampleWithRequiredData: IFrequency = {
  id: 8994,
};

export const sampleWithPartialData: IFrequency = {
  id: 6035,
  sunday: true,
  tuesday: false,
  wednesday: false,
  thursday: false,
  friday: false,
};

export const sampleWithFullData: IFrequency = {
  id: 20385,
  type: FrequencyType['WEEKLY'],
  saturday: false,
  sunday: true,
  monday: true,
  tuesday: true,
  wednesday: false,
  thursday: true,
  friday: false,
};

export const sampleWithNewData: NewFrequency = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
