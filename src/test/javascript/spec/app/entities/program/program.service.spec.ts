import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { ProgramService } from 'app/entities/program/program.service';
import { IProgram, Program } from 'app/shared/model/program.model';
import { EVENTTYPE } from 'app/shared/model/enumerations/eventtype.model';
import { EVENTLOCATION } from 'app/shared/model/enumerations/eventlocation.model';
import { LANGARMENU } from 'app/shared/model/enumerations/langarmenu.model';
import { EventStatus } from 'app/shared/model/enumerations/event-status.model';

describe('Service Tests', () => {
  describe('Program Service', () => {
    let injector: TestBed;
    let service: ProgramService;
    let httpMock: HttpTestingController;
    let elemDefault: IProgram;
    let expectedResult: IProgram | IProgram[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(ProgramService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Program(
        0,
        EVENTTYPE.SUKHMANI_SAHIB,
        EVENTLOCATION.HALL_2_GNSG,
        currentDate,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        false,
        LANGARMENU.SIMPLE_JALEBI_SHAHIPANEER,
        currentDate,
        0,
        0,
        0,
        0,
        'AAAAAAA',
        currentDate,
        EventStatus.BOOKED
      );
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            eventDateTime: currentDate.format(DATE_TIME_FORMAT),
            langarTime: currentDate.format(DATE_TIME_FORMAT),
            bookingDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Program', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            eventDateTime: currentDate.format(DATE_TIME_FORMAT),
            langarTime: currentDate.format(DATE_TIME_FORMAT),
            bookingDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            eventDateTime: currentDate,
            langarTime: currentDate,
            bookingDate: currentDate
          },
          returnedFromService
        );

        service.create(new Program()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Program', () => {
        const returnedFromService = Object.assign(
          {
            eventType: 'BBBBBB',
            eventLocation: 'BBBBBB',
            eventDateTime: currentDate.format(DATE_TIME_FORMAT),
            family: 'BBBBBB',
            phoneNumber: 'BBBBBB',
            email: 'BBBBBB',
            address: 'BBBBBB',
            withLangar: true,
            langarMenu: 'BBBBBB',
            langarTime: currentDate.format(DATE_TIME_FORMAT),
            dueAmt: 1,
            paidAmt: 1,
            balAmt: 1,
            recieptNumber: 1,
            remark: 'BBBBBB',
            bookingDate: currentDate.format(DATE_TIME_FORMAT),
            status: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            eventDateTime: currentDate,
            langarTime: currentDate,
            bookingDate: currentDate
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Program', () => {
        const returnedFromService = Object.assign(
          {
            eventType: 'BBBBBB',
            eventLocation: 'BBBBBB',
            eventDateTime: currentDate.format(DATE_TIME_FORMAT),
            family: 'BBBBBB',
            phoneNumber: 'BBBBBB',
            email: 'BBBBBB',
            address: 'BBBBBB',
            withLangar: true,
            langarMenu: 'BBBBBB',
            langarTime: currentDate.format(DATE_TIME_FORMAT),
            dueAmt: 1,
            paidAmt: 1,
            balAmt: 1,
            recieptNumber: 1,
            remark: 'BBBBBB',
            bookingDate: currentDate.format(DATE_TIME_FORMAT),
            status: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            eventDateTime: currentDate,
            langarTime: currentDate,
            bookingDate: currentDate
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Program', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
