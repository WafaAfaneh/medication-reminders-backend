import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TimeOfDayComponent } from '../list/time-of-day.component';
import { TimeOfDayDetailComponent } from '../detail/time-of-day-detail.component';
import { TimeOfDayUpdateComponent } from '../update/time-of-day-update.component';
import { TimeOfDayRoutingResolveService } from './time-of-day-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const timeOfDayRoute: Routes = [
  {
    path: '',
    component: TimeOfDayComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TimeOfDayDetailComponent,
    resolve: {
      timeOfDay: TimeOfDayRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TimeOfDayUpdateComponent,
    resolve: {
      timeOfDay: TimeOfDayRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TimeOfDayUpdateComponent,
    resolve: {
      timeOfDay: TimeOfDayRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(timeOfDayRoute)],
  exports: [RouterModule],
})
export class TimeOfDayRoutingModule {}
