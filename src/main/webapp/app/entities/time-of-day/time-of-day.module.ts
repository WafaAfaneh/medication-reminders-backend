import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TimeOfDayComponent } from './list/time-of-day.component';
import { TimeOfDayDetailComponent } from './detail/time-of-day-detail.component';
import { TimeOfDayUpdateComponent } from './update/time-of-day-update.component';
import { TimeOfDayDeleteDialogComponent } from './delete/time-of-day-delete-dialog.component';
import { TimeOfDayRoutingModule } from './route/time-of-day-routing.module';

@NgModule({
  imports: [SharedModule, TimeOfDayRoutingModule],
  declarations: [TimeOfDayComponent, TimeOfDayDetailComponent, TimeOfDayUpdateComponent, TimeOfDayDeleteDialogComponent],
})
export class TimeOfDayModule {}
