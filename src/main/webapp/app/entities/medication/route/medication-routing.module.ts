import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MedicationComponent } from '../list/medication.component';
import { MedicationDetailComponent } from '../detail/medication-detail.component';
import { MedicationUpdateComponent } from '../update/medication-update.component';
import { MedicationRoutingResolveService } from './medication-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const medicationRoute: Routes = [
  {
    path: '',
    component: MedicationComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MedicationDetailComponent,
    resolve: {
      medication: MedicationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MedicationUpdateComponent,
    resolve: {
      medication: MedicationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MedicationUpdateComponent,
    resolve: {
      medication: MedicationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(medicationRoute)],
  exports: [RouterModule],
})
export class MedicationRoutingModule {}
