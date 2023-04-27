import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ReminderComponent } from './list/reminder.component';
import { ReminderDetailComponent } from './detail/reminder-detail.component';
import { ReminderUpdateComponent } from './update/reminder-update.component';
import { ReminderDeleteDialogComponent } from './delete/reminder-delete-dialog.component';
import { ReminderRoutingModule } from './route/reminder-routing.module';

@NgModule({
  imports: [SharedModule, ReminderRoutingModule],
  declarations: [ReminderComponent, ReminderDetailComponent, ReminderUpdateComponent, ReminderDeleteDialogComponent],
})
export class ReminderModule {}
