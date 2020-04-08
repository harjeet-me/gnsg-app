import { Moment } from 'moment';
import { ISevadar } from 'app/shared/model/sevadar.model';
import { EVENTTYPE } from 'app/shared/model/enumerations/eventtype.model';
import { EVENTLOCATION } from 'app/shared/model/enumerations/eventlocation.model';
import { LANGARMENU } from 'app/shared/model/enumerations/langarmenu.model';
import { EventStatus } from 'app/shared/model/enumerations/event-status.model';

export interface IProgram {
  id?: number;
  programType?: EVENTTYPE;
  location?: EVENTLOCATION;
  etime?: Moment;
  family?: string;
  phoneNumber?: string;
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
  sevadars?: ISevadar[];
}

export class Program implements IProgram {
  constructor(
    public id?: number,
    public programType?: EVENTTYPE,
    public location?: EVENTLOCATION,
    public etime?: Moment,
    public family?: string,
    public phoneNumber?: string,
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
    public sevadars?: ISevadar[]
  ) {
    this.withLangar = this.withLangar || false;
  }
}
