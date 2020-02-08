import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IEmployee, Employee } from 'app/shared/model/employee.model';
import { EmployeeService } from './employee.service';

@Component({
  selector: 'jhi-employee-update',
  templateUrl: './employee-update.component.html'
})
export class EmployeeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    firstName: [],
    lastName: [],
    email: [],
    phoneNumber: [],
    sevaStartDate: [],
    sevaEndDate: [],
    isValid: []
  });

  constructor(protected employeeService: EmployeeService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ employee }) => {
      if (!employee.id) {
        const today = moment().startOf('day');
        employee.sevaStartDate = today;
        employee.sevaEndDate = today;
      }

      this.updateForm(employee);
    });
  }

  updateForm(employee: IEmployee): void {
    this.editForm.patchValue({
      id: employee.id,
      firstName: employee.firstName,
      lastName: employee.lastName,
      email: employee.email,
      phoneNumber: employee.phoneNumber,
      sevaStartDate: employee.sevaStartDate ? employee.sevaStartDate.format(DATE_TIME_FORMAT) : null,
      sevaEndDate: employee.sevaEndDate ? employee.sevaEndDate.format(DATE_TIME_FORMAT) : null,
      isValid: employee.isValid
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const employee = this.createFromForm();
    if (employee.id !== undefined) {
      this.subscribeToSaveResponse(this.employeeService.update(employee));
    } else {
      this.subscribeToSaveResponse(this.employeeService.create(employee));
    }
  }

  private createFromForm(): IEmployee {
    return {
      ...new Employee(),
      id: this.editForm.get(['id'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      sevaStartDate: this.editForm.get(['sevaStartDate'])!.value
        ? moment(this.editForm.get(['sevaStartDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      sevaEndDate: this.editForm.get(['sevaEndDate'])!.value
        ? moment(this.editForm.get(['sevaEndDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      isValid: this.editForm.get(['isValid'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEmployee>>): void {
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
}
