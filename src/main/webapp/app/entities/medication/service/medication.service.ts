import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMedication, NewMedication } from '../medication.model';

export type PartialUpdateMedication = Partial<IMedication> & Pick<IMedication, 'id'>;

export type EntityResponseType = HttpResponse<IMedication>;
export type EntityArrayResponseType = HttpResponse<IMedication[]>;

@Injectable({ providedIn: 'root' })
export class MedicationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/medications');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(medication: NewMedication): Observable<EntityResponseType> {
    return this.http.post<IMedication>(this.resourceUrl, medication, { observe: 'response' });
  }

  update(medication: IMedication): Observable<EntityResponseType> {
    return this.http.put<IMedication>(`${this.resourceUrl}/${this.getMedicationIdentifier(medication)}`, medication, {
      observe: 'response',
    });
  }

  partialUpdate(medication: PartialUpdateMedication): Observable<EntityResponseType> {
    return this.http.patch<IMedication>(`${this.resourceUrl}/${this.getMedicationIdentifier(medication)}`, medication, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMedication>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMedication[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMedicationIdentifier(medication: Pick<IMedication, 'id'>): number {
    return medication.id;
  }

  compareMedication(o1: Pick<IMedication, 'id'> | null, o2: Pick<IMedication, 'id'> | null): boolean {
    return o1 && o2 ? this.getMedicationIdentifier(o1) === this.getMedicationIdentifier(o2) : o1 === o2;
  }

  addMedicationToCollectionIfMissing<Type extends Pick<IMedication, 'id'>>(
    medicationCollection: Type[],
    ...medicationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const medications: Type[] = medicationsToCheck.filter(isPresent);
    if (medications.length > 0) {
      const medicationCollectionIdentifiers = medicationCollection.map(medicationItem => this.getMedicationIdentifier(medicationItem)!);
      const medicationsToAdd = medications.filter(medicationItem => {
        const medicationIdentifier = this.getMedicationIdentifier(medicationItem);
        if (medicationCollectionIdentifiers.includes(medicationIdentifier)) {
          return false;
        }
        medicationCollectionIdentifiers.push(medicationIdentifier);
        return true;
      });
      return [...medicationsToAdd, ...medicationCollection];
    }
    return medicationCollection;
  }
}
