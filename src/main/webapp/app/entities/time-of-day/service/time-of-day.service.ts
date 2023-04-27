import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITimeOfDay, NewTimeOfDay } from '../time-of-day.model';

export type PartialUpdateTimeOfDay = Partial<ITimeOfDay> & Pick<ITimeOfDay, 'id'>;

type RestOf<T extends ITimeOfDay | NewTimeOfDay> = Omit<T, 'time'> & {
  time?: string | null;
};

export type RestTimeOfDay = RestOf<ITimeOfDay>;

export type NewRestTimeOfDay = RestOf<NewTimeOfDay>;

export type PartialUpdateRestTimeOfDay = RestOf<PartialUpdateTimeOfDay>;

export type EntityResponseType = HttpResponse<ITimeOfDay>;
export type EntityArrayResponseType = HttpResponse<ITimeOfDay[]>;

@Injectable({ providedIn: 'root' })
export class TimeOfDayService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/time-of-days');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(timeOfDay: NewTimeOfDay): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(timeOfDay);
    return this.http
      .post<RestTimeOfDay>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(timeOfDay: ITimeOfDay): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(timeOfDay);
    return this.http
      .put<RestTimeOfDay>(`${this.resourceUrl}/${this.getTimeOfDayIdentifier(timeOfDay)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(timeOfDay: PartialUpdateTimeOfDay): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(timeOfDay);
    return this.http
      .patch<RestTimeOfDay>(`${this.resourceUrl}/${this.getTimeOfDayIdentifier(timeOfDay)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTimeOfDay>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTimeOfDay[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTimeOfDayIdentifier(timeOfDay: Pick<ITimeOfDay, 'id'>): number {
    return timeOfDay.id;
  }

  compareTimeOfDay(o1: Pick<ITimeOfDay, 'id'> | null, o2: Pick<ITimeOfDay, 'id'> | null): boolean {
    return o1 && o2 ? this.getTimeOfDayIdentifier(o1) === this.getTimeOfDayIdentifier(o2) : o1 === o2;
  }

  addTimeOfDayToCollectionIfMissing<Type extends Pick<ITimeOfDay, 'id'>>(
    timeOfDayCollection: Type[],
    ...timeOfDaysToCheck: (Type | null | undefined)[]
  ): Type[] {
    const timeOfDays: Type[] = timeOfDaysToCheck.filter(isPresent);
    if (timeOfDays.length > 0) {
      const timeOfDayCollectionIdentifiers = timeOfDayCollection.map(timeOfDayItem => this.getTimeOfDayIdentifier(timeOfDayItem)!);
      const timeOfDaysToAdd = timeOfDays.filter(timeOfDayItem => {
        const timeOfDayIdentifier = this.getTimeOfDayIdentifier(timeOfDayItem);
        if (timeOfDayCollectionIdentifiers.includes(timeOfDayIdentifier)) {
          return false;
        }
        timeOfDayCollectionIdentifiers.push(timeOfDayIdentifier);
        return true;
      });
      return [...timeOfDaysToAdd, ...timeOfDayCollection];
    }
    return timeOfDayCollection;
  }

  protected convertDateFromClient<T extends ITimeOfDay | NewTimeOfDay | PartialUpdateTimeOfDay>(timeOfDay: T): RestOf<T> {
    return {
      ...timeOfDay,
      time: timeOfDay.time?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTimeOfDay: RestTimeOfDay): ITimeOfDay {
    return {
      ...restTimeOfDay,
      time: restTimeOfDay.time ? dayjs(restTimeOfDay.time) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTimeOfDay>): HttpResponse<ITimeOfDay> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTimeOfDay[]>): HttpResponse<ITimeOfDay[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
