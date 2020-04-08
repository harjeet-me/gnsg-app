import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { ISevadar, Sevadar } from 'app/shared/model/sevadar.model';
import { SevadarService } from './sevadar.service';
import { IProgram } from 'app/shared/model/program.model';
import { ProgramService } from 'app/entities/program/program.service';

@Component({
  selector: 'jhi-sevadar-update',
  templateUrl: './sevadar-update.component.html'
})
export class SevadarUpdateComponent implements OnInit {
  isSaving = false;
  programs: IProgram[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    email: [],
    phoneNumber: [],
    sevaStartDate: [],
    sevaEndDate: [],
    isValid: [],
    program: []
  });

  constructor(
    protected sevadarService: SevadarService,
    protected programService: ProgramService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sevadar }) => {
      if (!sevadar.id) {
        const today = moment().startOf('day');
        sevadar.sevaStartDate = today;
        sevadar.sevaEndDate = today;
      }

      this.updateForm(sevadar);

      this.programService.query().subscribe((res: HttpResponse<IProgram[]>) => (this.programs = res.body || []));
    });
  }

  updateForm(sevadar: ISevadar): void {
    this.editForm.patchValue({
      id: sevadar.id,
      name: sevadar.name,
      email: sevadar.email,
      phoneNumber: sevadar.phoneNumber,
      sevaStartDate: sevadar.sevaStartDate ? sevadar.sevaStartDate.format(DATE_TIME_FORMAT) : null,
      sevaEndDate: sevadar.sevaEndDate ? sevadar.sevaEndDate.format(DATE_TIME_FORMAT) : null,
      isValid: sevadar.isValid,
      program: sevadar.program
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sevadar = this.createFromForm();
    if (sevadar.id !== undefined) {
      this.subscribeToSaveResponse(this.sevadarService.update(sevadar));
    } else {
      this.subscribeToSaveResponse(this.sevadarService.create(sevadar));
    }
  }

  private createFromForm(): ISevadar {
    return {
      ...new Sevadar(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      email: this.editForm.get(['email'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      sevaStartDate: this.editForm.get(['sevaStartDate'])!.value
        ? moment(this.editForm.get(['sevaStartDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      sevaEndDate: this.editForm.get(['sevaEndDate'])!.value
        ? moment(this.editForm.get(['sevaEndDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      isValid: this.editForm.get(['isValid'])!.value,
      program: this.editForm.get(['program'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISevadar>>): void {
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

  trackById(index: number, item: IProgram): any {
    return item.id;
  }
}
