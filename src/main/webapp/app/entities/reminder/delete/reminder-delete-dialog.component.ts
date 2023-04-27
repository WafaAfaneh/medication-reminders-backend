import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IReminder } from '../reminder.model';
import { ReminderService } from '../service/reminder.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './reminder-delete-dialog.component.html',
})
export class ReminderDeleteDialogComponent {
  reminder?: IReminder;

  constructor(protected reminderService: ReminderService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.reminderService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
