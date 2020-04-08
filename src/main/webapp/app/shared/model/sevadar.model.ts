import { Moment } from 'moment';
import { IProgram } from 'app/shared/model/program.model';

export interface ISevadar {
  id?: number;
  name?: string;
  email?: string;
  phoneNumber?: string;
  sevaStartDate?: Moment;
  sevaEndDate?: Moment;
  isValid?: boolean;
  program?: IProgram;
}

export class Sevadar implements ISevadar {
  constructor(
    public id?: number,
    public name?: string,
    public email?: string,
    public phoneNumber?: string,
    public sevaStartDate?: Moment,
    public sevaEndDate?: Moment,
    public isValid?: boolean,
    public program?: IProgram
  ) {
    this.isValid = this.isValid || false;
  }
}
