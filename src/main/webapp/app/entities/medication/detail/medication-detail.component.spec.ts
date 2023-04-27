import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MedicationDetailComponent } from './medication-detail.component';

describe('Medication Management Detail Component', () => {
  let comp: MedicationDetailComponent;
  let fixture: ComponentFixture<MedicationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MedicationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ medication: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MedicationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MedicationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load medication on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.medication).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
