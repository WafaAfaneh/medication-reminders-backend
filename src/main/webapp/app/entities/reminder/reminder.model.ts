import dayjs from 'dayjs/esm';
import { INotification } from 'app/entities/notification/notification.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface IReminder {
  id: number;
  date?: dayjs.Dayjs | null;
  status?: Status | null;
  notification?: Pick<INotification, 'id'> | null;
}

export type NewReminder = Omit<IReminder, 'id'> & { id: null };
