import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FrequencyComponent } from '../list/frequency.component';
import { FrequencyDetailComponent } from '../detail/frequency-detail.component';
import { FrequencyUpdateComponent } from '../update/frequency-update.component';
import { FrequencyRoutingResolveService } from './frequency-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const frequencyRoute: Routes = [
  {
    path: '',
    component: FrequencyComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FrequencyDetailComponent,
    resolve: {
      frequency: FrequencyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FrequencyUpdateComponent,
    resolve: {
      frequency: FrequencyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FrequencyUpdateComponent,
    resolve: {
      frequency: FrequencyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(frequencyRoute)],
  exports: [RouterModule],
})
export class FrequencyRoutingModule {}
