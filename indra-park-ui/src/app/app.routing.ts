import { Routes } from '@angular/router';

import { FullComponent } from './layouts/full/full.component';
import { FormEntradaComponent } from './view/form-entrada/form-entrada.component';
import { FormSaidaComponent } from './view/form-saida/form-saida.component';
import { PaginaInicialComponent } from './view/pagina-inicial/pagina-inicial.component';
import { AppComponent } from './app.component';

export const AppRoutes: Routes = [
  {
    path: '',
    component: AppComponent,
    children: [
      {
        path: '',
        component: FullComponent,
        children: [
          {
            path: '',
            component: PaginaInicialComponent
          },
          {
            path: 'entrada',
            component: FormEntradaComponent
          },
          {
            path: 'saida',
            component: FormSaidaComponent,
            data: { data: '' }
          }
        ]
      }
    ]
  },
];
