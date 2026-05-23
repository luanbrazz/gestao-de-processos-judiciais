import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'processos', pathMatch: 'full' },
  {
    path: 'processos',
    loadComponent: () =>
      import('./features/processos/pages/processo-list/processo-list.component')
        .then(m => m.ProcessoListComponent)
  },
  { path: '**', redirectTo: 'processos' }
];
