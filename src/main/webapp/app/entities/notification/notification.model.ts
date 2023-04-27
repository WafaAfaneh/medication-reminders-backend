import dayjs from 'dayjs/esm';
import { IFrequency } from 'app/entities/frequency/frequency.model';

export interface INotification {
  id: number;
  displayName?: string | null;
  notes?: string | null;
  startDate?: dayjs.Dayjs | null;
  frequency?: Pick<IFrequency, 'id'> | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
