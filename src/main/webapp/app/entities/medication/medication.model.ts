import { INotification } from 'app/entities/notification/notification.model';
import { IUser } from 'app/entities/user/user.model';
import { MedicationType } from 'app/entities/enumerations/medication-type.model';

export interface IMedication {
  id: number;
  name?: string | null;
  dosageQuantity?: number | null;
  type?: MedicationType | null;
  active?: boolean | null;
  notification?: Pick<INotification, 'id'> | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewMedication = Omit<IMedication, 'id'> & { id: null };
