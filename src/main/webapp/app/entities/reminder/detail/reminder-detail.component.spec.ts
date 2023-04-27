import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ReminderDetailComponent } from './reminder-detail.component';

describe('Reminder Management Detail Component', () => {
  let comp: ReminderDetailComponent;
  let fixture: ComponentFixture<ReminderDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReminderDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ reminder: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ReminderDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ReminderDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load reminder on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.reminder).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
