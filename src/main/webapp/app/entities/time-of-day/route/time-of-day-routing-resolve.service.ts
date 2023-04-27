import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITimeOfDay } from '../time-of-day.model';
import { TimeOfDayService } from '../service/time-of-day.service';

@Injectable({ providedIn: 'root' })
export class TimeOfDayRoutingResolveService implements Resolve<ITimeOfDay | null> {
  constructor(protected service: TimeOfDayService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITimeOfDay | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((timeOfDay: HttpResponse<ITimeOfDay>) => {
          if (timeOfDay.body) {
            return of(timeOfDay.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
