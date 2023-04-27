import { MedicationType } from 'app/entities/enumerations/medication-type.model';

import { IMedication, NewMedication } from './medication.model';

export const sampleWithRequiredData: IMedication = {
  id: 73796,
};

export const sampleWithPartialData: IMedication = {
  id: 30713,
  dosageQuantity: 9863,
};

export const sampleWithFullData: IMedication = {
  id: 88634,
  name: 'Rupee intangible',
  dosageQuantity: 63796,
  type: MedicationType['CREAM'],
  active: true,
};

export const sampleWithNewData: NewMedication = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
