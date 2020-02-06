import { Moment } from 'moment';
import { IProgram } from 'app/shared/model/program.model';
import { IFile } from 'app/shared/model/file.model';

export interface IEmployee {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  sevaStartDate?: Moment;
  sevaEndDate?: Moment;
  isValid?: boolean;
  pathiSinghs?: IProgram[];
  ragiSinghs?: IProgram[];
  bookingBies?: IProgram[];
  updatedBies?: IProgram[];
  files?: IFile[];
}

export class Employee implements IEmployee {
  constructor(
    public id?: number,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public phoneNumber?: string,
    public sevaStartDate?: Moment,
    public sevaEndDate?: Moment,
    public isValid?: boolean,
    public pathiSinghs?: IProgram[],
    public ragiSinghs?: IProgram[],
    public bookingBies?: IProgram[],
    public updatedBies?: IProgram[],
    public files?: IFile[]
  ) {
    this.isValid = this.isValid || false;
  }
}
