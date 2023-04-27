import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FrequencyDetailComponent } from './frequency-detail.component';

describe('Frequency Management Detail Component', () => {
  let comp: FrequencyDetailComponent;
  let fixture: ComponentFixture<FrequencyDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FrequencyDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ frequency: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FrequencyDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FrequencyDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load frequency on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.frequency).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
