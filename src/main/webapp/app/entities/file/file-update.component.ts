import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IFile, File } from 'app/shared/model/file.model';
import { FileService } from './file.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { IEmployee } from 'app/shared/model/employee.model';
import { EmployeeService } from 'app/entities/employee/employee.service';

@Component({
  selector: 'jhi-file-update',
  templateUrl: './file-update.component.html'
})
export class FileUpdateComponent implements OnInit {
  isSaving = false;

  employees: IEmployee[] = [];

  editForm = this.fb.group({
    id: [],
    attachment: [],
    attachmentContentType: [],
    employee: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected fileService: FileService,
    protected employeeService: EmployeeService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ file }) => {
      this.updateForm(file);

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

  updateForm(file: IFile): void {
    this.editForm.patchValue({
      id: file.id,
      attachment: file.attachment,
      attachmentContentType: file.attachmentContentType,
      employee: file.employee
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(
        new JhiEventWithContent<AlertError>('gmsApp.error', { ...err, key: 'error.file.' + err.key })
      );
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const file = this.createFromForm();
    if (file.id !== undefined) {
      this.subscribeToSaveResponse(this.fileService.update(file));
    } else {
      this.subscribeToSaveResponse(this.fileService.create(file));
    }
  }

  private createFromForm(): IFile {
    return {
      ...new File(),
      id: this.editForm.get(['id'])!.value,
      attachmentContentType: this.editForm.get(['attachmentContentType'])!.value,
      attachment: this.editForm.get(['attachment'])!.value,
      employee: this.editForm.get(['employee'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFile>>): void {
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
