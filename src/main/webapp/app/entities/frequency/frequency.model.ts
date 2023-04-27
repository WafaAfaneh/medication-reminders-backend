import { FrequencyType } from 'app/entities/enumerations/frequency-type.model';

export interface IFrequency {
  id: number;
  type?: FrequencyType | null;
  saturday?: boolean | null;
  sunday?: boolean | null;
  monday?: boolean | null;
  tuesday?: boolean | null;
  wednesday?: boolean | null;
  thursday?: boolean | null;
  friday?: boolean | null;
}

export type NewFrequency = Omit<IFrequency, 'id'> & { id: null };
