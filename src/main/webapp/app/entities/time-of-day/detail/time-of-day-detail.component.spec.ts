import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TimeOfDayDetailComponent } from './time-of-day-detail.component';

describe('TimeOfDay Management Detail Component', () => {
  let comp: TimeOfDayDetailComponent;
  let fixture: ComponentFixture<TimeOfDayDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TimeOfDayDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ timeOfDay: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TimeOfDayDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TimeOfDayDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load timeOfDay on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.timeOfDay).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
