import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FrequencyComponent } from './list/frequency.component';
import { FrequencyDetailComponent } from './detail/frequency-detail.component';
import { FrequencyUpdateComponent } from './update/frequency-update.component';
import { FrequencyDeleteDialogComponent } from './delete/frequency-delete-dialog.component';
import { FrequencyRoutingModule } from './route/frequency-routing.module';

@NgModule({
  imports: [SharedModule, FrequencyRoutingModule],
  declarations: [FrequencyComponent, FrequencyDetailComponent, FrequencyUpdateComponent, FrequencyDeleteDialogComponent],
})
export class FrequencyModule {}
