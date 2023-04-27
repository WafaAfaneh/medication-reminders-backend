import dayjs from 'dayjs/esm';

import { Status } from 'app/entities/enumerations/status.model';

import { IReminder, NewReminder } from './reminder.model';

export const sampleWithRequiredData: IReminder = {
  id: 22465,
};

export const sampleWithPartialData: IReminder = {
  id: 52222,
  date: dayjs('2023-03-27T10:55'),
  status: Status['NOT_INTERACTED'],
};

export const sampleWithFullData: IReminder = {
  id: 27249,
  date: dayjs('2023-03-27T17:39'),
  status: Status['TAKEN'],
};

export const sampleWithNewData: NewReminder = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
