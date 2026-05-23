import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'processos', pathMatch: 'full' },
  {
    path: 'processos',
    loadComponent: () =>
      import('./features/processos/pages/processo-list/processo-list.component')
        .then(m => m.ProcessoListComponent)
  },
  {
    path: 'processos/novo',
    loadComponent: () =>
      import('./features/processos/pages/processo-form/processo-form.component')
        .then(m => m.ProcessoFormComponent)
  },
  {
    path: 'processos/:id',
    loadComponent: () =>
      import('./features/processos/pages/processo-detail/processo-detail.component')
        .then(m => m.ProcessoDetailComponent)
  },
  {
    path: 'processos/:id/editar',
    loadComponent: () =>
      import('./features/processos/pages/processo-form/processo-form.component')
        .then(m => m.ProcessoFormComponent)
  },
  { path: '**', redirectTo: 'processos' }
];
