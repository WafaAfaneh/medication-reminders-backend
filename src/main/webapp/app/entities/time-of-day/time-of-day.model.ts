import dayjs from 'dayjs/esm';
import { INotification } from 'app/entities/notification/notification.model';

export interface ITimeOfDay {
  id: number;
  time?: dayjs.Dayjs | null;
  notification?: Pick<INotification, 'id'> | null;
}

export type NewTimeOfDay = Omit<ITimeOfDay, 'id'> & { id: null };
