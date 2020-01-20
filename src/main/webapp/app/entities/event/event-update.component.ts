import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IEvent, Event } from 'app/shared/model/event.model';
import { EventService } from './event.service';
import { IEmployee } from 'app/shared/model/employee.model';
import { EmployeeService } from 'app/entities/employee/employee.service';

@Component({
  selector: 'jhi-event-update',
  templateUrl: './event-update.component.html'
})
export class EventUpdateComponent implements OnInit {
  isSaving = false;

  employees: IEmployee[] = [];

  editForm = this.fb.group({
    id: [],
    eventType: [],
    eventLocation: [],
    eventDateTime: [],
    family: [],
    phoneNumber: [],
    email: [],
    address: [],
    withLangar: [],
    langarMenu: [],
    langarTime: [],
    dueAmt: [],
    paidAmt: [],
    balAmt: [],
    recieptNumber: [],
    remark: [],
    bookingDate: [],
    status: [],
    employee: [],
    employee: [],
    employee: [],
    employee: []
  });

  constructor(
    protected eventService: EventService,
    protected employeeService: EmployeeService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ event }) => {
      this.updateForm(event);

      this.employeeService
        .query()
        .pipe(
          map((res: HttpResponse<IEmployee[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: IEmployee[]) => (this.employees = resBody));
    });
  }

  updateForm(event: IEvent): void {
    this.editForm.patchValue({
      id: event.id,
      eventType: event.eventType,
      eventLocation: event.eventLocation,
      eventDateTime: event.eventDateTime != null ? event.eventDateTime.format(DATE_TIME_FORMAT) : null,
      family: event.family,
      phoneNumber: event.phoneNumber,
      email: event.email,
      address: event.address,
      withLangar: event.withLangar,
      langarMenu: event.langarMenu,
      langarTime: event.langarTime != null ? event.langarTime.format(DATE_TIME_FORMAT) : null,
      dueAmt: event.dueAmt,
      paidAmt: event.paidAmt,
      balAmt: event.balAmt,
      recieptNumber: event.recieptNumber,
      remark: event.remark,
      bookingDate: event.bookingDate != null ? event.bookingDate.format(DATE_TIME_FORMAT) : null,
      status: event.status,
      employee: event.employee,
      employee: event.employee,
      employee: event.employee,
      employee: event.employee
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const event = this.createFromForm();
    if (event.id !== undefined) {
      this.subscribeToSaveResponse(this.eventService.update(event));
    } else {
      this.subscribeToSaveResponse(this.eventService.create(event));
    }
  }

  private createFromForm(): IEvent {
    return {
      ...new Event(),
      id: this.editForm.get(['id'])!.value,
      eventType: this.editForm.get(['eventType'])!.value,
      eventLocation: this.editForm.get(['eventLocation'])!.value,
      eventDateTime:
        this.editForm.get(['eventDateTime'])!.value != null
          ? moment(this.editForm.get(['eventDateTime'])!.value, DATE_TIME_FORMAT)
          : undefined,
      family: this.editForm.get(['family'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      email: this.editForm.get(['email'])!.value,
      address: this.editForm.get(['address'])!.value,
      withLangar: this.editForm.get(['withLangar'])!.value,
      langarMenu: this.editForm.get(['langarMenu'])!.value,
      langarTime:
        this.editForm.get(['langarTime'])!.value != null ? moment(this.editForm.get(['langarTime'])!.value, DATE_TIME_FORMAT) : undefined,
      dueAmt: this.editForm.get(['dueAmt'])!.value,
      paidAmt: this.editForm.get(['paidAmt'])!.value,
      balAmt: this.editForm.get(['balAmt'])!.value,
      recieptNumber: this.editForm.get(['recieptNumber'])!.value,
      remark: this.editForm.get(['remark'])!.value,
      bookingDate:
        this.editForm.get(['bookingDate'])!.value != null ? moment(this.editForm.get(['bookingDate'])!.value, DATE_TIME_FORMAT) : undefined,
      status: this.editForm.get(['status'])!.value,
      employee: this.editForm.get(['employee'])!.value,
      employee: this.editForm.get(['employee'])!.value,
      employee: this.editForm.get(['employee'])!.value,
      employee: this.editForm.get(['employee'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvent>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IEmployee): any {
    return item.id;
  }
}
