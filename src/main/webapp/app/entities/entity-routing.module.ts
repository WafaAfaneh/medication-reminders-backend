import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'medication',
        data: { pageTitle: 'medicationReminderServerApp.medication.home.title' },
        loadChildren: () => import('./medication/medication.module').then(m => m.MedicationModule),
      },
      {
        path: 'frequency',
        data: { pageTitle: 'medicationReminderServerApp.frequency.home.title' },
        loadChildren: () => import('./frequency/frequency.module').then(m => m.FrequencyModule),
      },
      {
        path: 'notification',
        data: { pageTitle: 'medicationReminderServerApp.notification.home.title' },
        loadChildren: () => import('./notification/notification.module').then(m => m.NotificationModule),
      },
      {
        path: 'time-of-day',
        data: { pageTitle: 'medicationReminderServerApp.timeOfDay.home.title' },
        loadChildren: () => import('./time-of-day/time-of-day.module').then(m => m.TimeOfDayModule),
      },
      {
        path: 'reminder',
        data: { pageTitle: 'medicationReminderServerApp.reminder.home.title' },
        loadChildren: () => import('./reminder/reminder.module').then(m => m.ReminderModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
