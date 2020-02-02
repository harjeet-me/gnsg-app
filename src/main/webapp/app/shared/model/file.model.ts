import { IEmployee } from 'app/shared/model/employee.model';

export interface IFile {
  id?: number;
  attachmentContentType?: string;
  attachment?: any;
  employee?: IEmployee;
}

export class File implements IFile {
  constructor(public id?: number, public attachmentContentType?: string, public attachment?: any, public employee?: IEmployee) {}
}
