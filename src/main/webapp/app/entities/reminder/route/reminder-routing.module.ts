import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ReminderComponent } from '../list/reminder.component';
import { ReminderDetailComponent } from '../detail/reminder-detail.component';
import { ReminderUpdateComponent } from '../update/reminder-update.component';
import { ReminderRoutingResolveService } from './reminder-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const reminderRoute: Routes = [
  {
    path: '',
    component: ReminderComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReminderDetailComponent,
    resolve: {
      reminder: ReminderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReminderUpdateComponent,
    resolve: {
      reminder: ReminderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReminderUpdateComponent,
    resolve: {
      reminder: ReminderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(reminderRoute)],
  exports: [RouterModule],
})
export class ReminderRoutingModule {}
