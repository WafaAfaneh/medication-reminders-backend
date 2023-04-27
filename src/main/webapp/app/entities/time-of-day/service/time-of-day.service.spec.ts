import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITimeOfDay } from '../time-of-day.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../time-of-day.test-samples';

import { TimeOfDayService, RestTimeOfDay } from './time-of-day.service';

const requireRestSample: RestTimeOfDay = {
  ...sampleWithRequiredData,
  time: sampleWithRequiredData.time?.toJSON(),
};

describe('TimeOfDay Service', () => {
  let service: TimeOfDayService;
  let httpMock: HttpTestingController;
  let expectedResult: ITimeOfDay | ITimeOfDay[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TimeOfDayService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a TimeOfDay', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const timeOfDay = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(timeOfDay).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TimeOfDay', () => {
      const timeOfDay = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(timeOfDay).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TimeOfDay', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TimeOfDay', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TimeOfDay', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTimeOfDayToCollectionIfMissing', () => {
      it('should add a TimeOfDay to an empty array', () => {
        const timeOfDay: ITimeOfDay = sampleWithRequiredData;
        expectedResult = service.addTimeOfDayToCollectionIfMissing([], timeOfDay);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(timeOfDay);
      });

      it('should not add a TimeOfDay to an array that contains it', () => {
        const timeOfDay: ITimeOfDay = sampleWithRequiredData;
        const timeOfDayCollection: ITimeOfDay[] = [
          {
            ...timeOfDay,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTimeOfDayToCollectionIfMissing(timeOfDayCollection, timeOfDay);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TimeOfDay to an array that doesn't contain it", () => {
        const timeOfDay: ITimeOfDay = sampleWithRequiredData;
        const timeOfDayCollection: ITimeOfDay[] = [sampleWithPartialData];
        expectedResult = service.addTimeOfDayToCollectionIfMissing(timeOfDayCollection, timeOfDay);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(timeOfDay);
      });

      it('should add only unique TimeOfDay to an array', () => {
        const timeOfDayArray: ITimeOfDay[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const timeOfDayCollection: ITimeOfDay[] = [sampleWithRequiredData];
        expectedResult = service.addTimeOfDayToCollectionIfMissing(timeOfDayCollection, ...timeOfDayArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const timeOfDay: ITimeOfDay = sampleWithRequiredData;
        const timeOfDay2: ITimeOfDay = sampleWithPartialData;
        expectedResult = service.addTimeOfDayToCollectionIfMissing([], timeOfDay, timeOfDay2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(timeOfDay);
        expect(expectedResult).toContain(timeOfDay2);
      });

      it('should accept null and undefined values', () => {
        const timeOfDay: ITimeOfDay = sampleWithRequiredData;
        expectedResult = service.addTimeOfDayToCollectionIfMissing([], null, timeOfDay, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(timeOfDay);
      });

      it('should return initial array if no TimeOfDay is added', () => {
        const timeOfDayCollection: ITimeOfDay[] = [sampleWithRequiredData];
        expectedResult = service.addTimeOfDayToCollectionIfMissing(timeOfDayCollection, undefined, null);
        expect(expectedResult).toEqual(timeOfDayCollection);
      });
    });

    describe('compareTimeOfDay', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTimeOfDay(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTimeOfDay(entity1, entity2);
        const compareResult2 = service.compareTimeOfDay(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTimeOfDay(entity1, entity2);
        const compareResult2 = service.compareTimeOfDay(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTimeOfDay(entity1, entity2);
        const compareResult2 = service.compareTimeOfDay(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
