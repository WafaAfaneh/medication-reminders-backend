import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReminder } from '../reminder.model';
import { ReminderService } from '../service/reminder.service';

@Injectable({ providedIn: 'root' })
export class ReminderRoutingResolveService implements Resolve<IReminder | null> {
  constructor(protected service: ReminderService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IReminder | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((reminder: HttpResponse<IReminder>) => {
          if (reminder.body) {
            return of(reminder.body);
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
