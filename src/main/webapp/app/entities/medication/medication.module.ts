import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MedicationComponent } from './list/medication.component';
import { MedicationDetailComponent } from './detail/medication-detail.component';
import { MedicationUpdateComponent } from './update/medication-update.component';
import { MedicationDeleteDialogComponent } from './delete/medication-delete-dialog.component';
import { MedicationRoutingModule } from './route/medication-routing.module';

@NgModule({
  imports: [SharedModule, MedicationRoutingModule],
  declarations: [MedicationComponent, MedicationDetailComponent, MedicationUpdateComponent, MedicationDeleteDialogComponent],
})
export class MedicationModule {}
