import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 30621,
};

export const sampleWithPartialData: INotification = {
  id: 55330,
  startDate: dayjs('2023-03-27'),
};

export const sampleWithFullData: INotification = {
  id: 65655,
  displayName: 'Practical',
  notes: 'Customizable synergies',
  startDate: dayjs('2023-03-27'),
};

export const sampleWithNewData: NewNotification = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
