import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'event',
        loadChildren: () => import('./event/event.module').then(m => m.GmsAppEventModule)
      },
      {
        path: 'task',
        loadChildren: () => import('./task/task.module').then(m => m.GmsAppTaskModule)
      },
      {
        path: 'employee',
        loadChildren: () => import('./employee/employee.module').then(m => m.GmsAppEmployeeModule)
      },
      {
        path: 'file',
        loadChildren: () => import('./file/file.module').then(m => m.GmsAppFileModule)
      },
      {
        path: 'program',
        loadChildren: () => import('./program/program.module').then(m => m.GmsAppProgramModule)
      },
      {
        path: 'sevadar',
        loadChildren: () => import('./sevadar/sevadar.module').then(m => m.GmsAppSevadarModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class GmsAppEntityModule {}
