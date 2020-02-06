import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { IProgram } from 'app/shared/model/program.model';

type EntityResponseType = HttpResponse<IProgram>;
type EntityArrayResponseType = HttpResponse<IProgram[]>;

@Injectable({ providedIn: 'root' })
export class ProgramService {
  public resourceUrl = SERVER_API_URL + 'api/programs';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/programs';

  constructor(protected http: HttpClient) {}

  create(program: IProgram): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(program);
    return this.http
      .post<IProgram>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(program: IProgram): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(program);
    return this.http
      .put<IProgram>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IProgram>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IProgram[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IProgram[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(program: IProgram): IProgram {
    const copy: IProgram = Object.assign({}, program, {
      eventDateTime: program.eventDateTime && program.eventDateTime.isValid() ? program.eventDateTime.toJSON() : undefined,
      langarTime: program.langarTime && program.langarTime.isValid() ? program.langarTime.toJSON() : undefined,
      bookingDate: program.bookingDate && program.bookingDate.isValid() ? program.bookingDate.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.eventDateTime = res.body.eventDateTime ? moment(res.body.eventDateTime) : undefined;
      res.body.langarTime = res.body.langarTime ? moment(res.body.langarTime) : undefined;
      res.body.bookingDate = res.body.bookingDate ? moment(res.body.bookingDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((program: IProgram) => {
        program.eventDateTime = program.eventDateTime ? moment(program.eventDateTime) : undefined;
        program.langarTime = program.langarTime ? moment(program.langarTime) : undefined;
        program.bookingDate = program.bookingDate ? moment(program.bookingDate) : undefined;
      });
    }
    return res;
  }
}
