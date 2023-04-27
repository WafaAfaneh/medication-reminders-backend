import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITimeOfDay } from '../time-of-day.model';
import { TimeOfDayService } from '../service/time-of-day.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './time-of-day-delete-dialog.component.html',
})
export class TimeOfDayDeleteDialogComponent {
  timeOfDay?: ITimeOfDay;

  constructor(protected timeOfDayService: TimeOfDayService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.timeOfDayService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
