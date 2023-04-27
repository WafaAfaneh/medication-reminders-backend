import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFrequency } from '../frequency.model';
import { FrequencyService } from '../service/frequency.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './frequency-delete-dialog.component.html',
})
export class FrequencyDeleteDialogComponent {
  frequency?: IFrequency;

  constructor(protected frequencyService: FrequencyService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.frequencyService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
