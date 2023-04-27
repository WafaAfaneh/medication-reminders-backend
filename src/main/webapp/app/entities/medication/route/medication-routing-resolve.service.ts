import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMedication } from '../medication.model';
import { MedicationService } from '../service/medication.service';

@Injectable({ providedIn: 'root' })
export class MedicationRoutingResolveService implements Resolve<IMedication | null> {
  constructor(protected service: MedicationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMedication | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((medication: HttpResponse<IMedication>) => {
          if (medication.body) {
            return of(medication.body);
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
