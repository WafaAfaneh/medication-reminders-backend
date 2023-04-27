import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFrequency } from '../frequency.model';
import { FrequencyService } from '../service/frequency.service';

@Injectable({ providedIn: 'root' })
export class FrequencyRoutingResolveService implements Resolve<IFrequency | null> {
  constructor(protected service: FrequencyService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFrequency | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((frequency: HttpResponse<IFrequency>) => {
          if (frequency.body) {
            return of(frequency.body);
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
