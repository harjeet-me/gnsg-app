import { Moment } from 'moment';
import { IEvent } from 'app/shared/model/event.model';

export interface IEmployee {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  sevaStartDate?: Moment;
  sevaEndDate?: Moment;
  isValid?: boolean;
  pathiSinghs?: IEvent[];
  ragiSinghs?: IEvent[];
  bookingBies?: IEvent[];
  updatedBies?: IEvent[];
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
    public pathiSinghs?: IEvent[],
    public ragiSinghs?: IEvent[],
    public bookingBies?: IEvent[],
    public updatedBies?: IEvent[]
  ) {
    this.isValid = this.isValid || false;
  }
}
