import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IProgram, Program } from 'app/shared/model/program.model';
import { ProgramService } from './program.service';
import { IEmployee } from 'app/shared/model/employee.model';
import { EmployeeService } from 'app/entities/employee/employee.service';

@Component({
  selector: 'jhi-program-update',
  templateUrl: './program-update.component.html'
})
export class ProgramUpdateComponent implements OnInit {
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
    protected programService: ProgramService,
    protected employeeService: EmployeeService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ program }) => {
      this.updateForm(program);

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

  updateForm(program: IProgram): void {
    this.editForm.patchValue({
      id: program.id,
      eventType: program.eventType,
      eventLocation: program.eventLocation,
      eventDateTime: program.eventDateTime != null ? program.eventDateTime.format(DATE_TIME_FORMAT) : null,
      family: program.family,
      phoneNumber: program.phoneNumber,
      email: program.email,
      address: program.address,
      withLangar: program.withLangar,
      langarMenu: program.langarMenu,
      langarTime: program.langarTime != null ? program.langarTime.format(DATE_TIME_FORMAT) : null,
      dueAmt: program.dueAmt,
      paidAmt: program.paidAmt,
      balAmt: program.balAmt,
      recieptNumber: program.recieptNumber,
      remark: program.remark,
      bookingDate: program.bookingDate != null ? program.bookingDate.format(DATE_TIME_FORMAT) : null,
      status: program.status,
      employee: program.employee,
      employee: program.employee,
      employee: program.employee,
      employee: program.employee
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const program = this.createFromForm();
    if (program.id !== undefined) {
      this.subscribeToSaveResponse(this.programService.update(program));
    } else {
      this.subscribeToSaveResponse(this.programService.create(program));
    }
  }

  private createFromForm(): IProgram {
    return {
      ...new Program(),
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgram>>): void {
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
