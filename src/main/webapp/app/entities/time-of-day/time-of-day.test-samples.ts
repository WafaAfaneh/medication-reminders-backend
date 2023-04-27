import dayjs from 'dayjs/esm';

import { ITimeOfDay, NewTimeOfDay } from './time-of-day.model';

export const sampleWithRequiredData: ITimeOfDay = {
  id: 60605,
};

export const sampleWithPartialData: ITimeOfDay = {
  id: 45316,
};

export const sampleWithFullData: ITimeOfDay = {
  id: 93152,
  time: dayjs('2023-03-27T14:39'),
};

export const sampleWithNewData: NewTimeOfDay = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
