import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFrequency, NewFrequency } from '../frequency.model';

export type PartialUpdateFrequency = Partial<IFrequency> & Pick<IFrequency, 'id'>;

export type EntityResponseType = HttpResponse<IFrequency>;
export type EntityArrayResponseType = HttpResponse<IFrequency[]>;

@Injectable({ providedIn: 'root' })
export class FrequencyService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/frequencies');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(frequency: NewFrequency): Observable<EntityResponseType> {
    return this.http.post<IFrequency>(this.resourceUrl, frequency, { observe: 'response' });
  }

  update(frequency: IFrequency): Observable<EntityResponseType> {
    return this.http.put<IFrequency>(`${this.resourceUrl}/${this.getFrequencyIdentifier(frequency)}`, frequency, { observe: 'response' });
  }

  partialUpdate(frequency: PartialUpdateFrequency): Observable<EntityResponseType> {
    return this.http.patch<IFrequency>(`${this.resourceUrl}/${this.getFrequencyIdentifier(frequency)}`, frequency, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFrequency>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFrequency[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFrequencyIdentifier(frequency: Pick<IFrequency, 'id'>): number {
    return frequency.id;
  }

  compareFrequency(o1: Pick<IFrequency, 'id'> | null, o2: Pick<IFrequency, 'id'> | null): boolean {
    return o1 && o2 ? this.getFrequencyIdentifier(o1) === this.getFrequencyIdentifier(o2) : o1 === o2;
  }

  addFrequencyToCollectionIfMissing<Type extends Pick<IFrequency, 'id'>>(
    frequencyCollection: Type[],
    ...frequenciesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const frequencies: Type[] = frequenciesToCheck.filter(isPresent);
    if (frequencies.length > 0) {
      const frequencyCollectionIdentifiers = frequencyCollection.map(frequencyItem => this.getFrequencyIdentifier(frequencyItem)!);
      const frequenciesToAdd = frequencies.filter(frequencyItem => {
        const frequencyIdentifier = this.getFrequencyIdentifier(frequencyItem);
        if (frequencyCollectionIdentifiers.includes(frequencyIdentifier)) {
          return false;
        }
        frequencyCollectionIdentifiers.push(frequencyIdentifier);
        return true;
      });
      return [...frequenciesToAdd, ...frequencyCollection];
    }
    return frequencyCollection;
  }
}
