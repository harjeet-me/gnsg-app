import { Moment } from 'moment';
import { IEmployee } from 'app/shared/model/employee.model';
import { EVENTTYPE } from 'app/shared/model/enumerations/eventtype.model';
import { EVENTLOCATION } from 'app/shared/model/enumerations/eventlocation.model';
import { LANGARMENU } from 'app/shared/model/enumerations/langarmenu.model';
import { EventStatus } from 'app/shared/model/enumerations/event-status.model';

export interface IProgram {
  id?: number;
  eventType?: EVENTTYPE;
  eventLocation?: EVENTLOCATION;
  eventDateTime?: Moment;
  family?: string;
  phoneNumber?: string;
  email?: string;
  address?: string;
  withLangar?: boolean;
  langarMenu?: LANGARMENU;
  langarTime?: Moment;
  dueAmt?: number;
  paidAmt?: number;
  balAmt?: number;
  recieptNumber?: number;
  remark?: string;
  bookingDate?: Moment;
  status?: EventStatus;
  employee?: IEmployee;
  employee?: IEmployee;
  employee?: IEmployee;
  employee?: IEmployee;
}

export class Program implements IProgram {
  constructor(
    public id?: number,
    public eventType?: EVENTTYPE,
    public eventLocation?: EVENTLOCATION,
    public eventDateTime?: Moment,
    public family?: string,
    public phoneNumber?: string,
    public email?: string,
    public address?: string,
    public withLangar?: boolean,
    public langarMenu?: LANGARMENU,
    public langarTime?: Moment,
    public dueAmt?: number,
    public paidAmt?: number,
    public balAmt?: number,
    public recieptNumber?: number,
    public remark?: string,
    public bookingDate?: Moment,
    public status?: EventStatus,
    public employee?: IEmployee,
    public employee?: IEmployee,
    public employee?: IEmployee,
    public employee?: IEmployee
  ) {
    this.withLangar = this.withLangar || false;
  }
}
