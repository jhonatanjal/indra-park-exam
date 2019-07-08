import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { FullComponent } from './layouts/full/full.component';
import { AppRoutes } from './app.routing';
import { RouterModule } from '@angular/router';
import { MatMenuModule } from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DemoMaterialModule } from './demo-material-module';
import { ReactiveFormsModule } from '@angular/forms';
import { OperacoesService } from './services/operacoes.service';
import { HttpClientModule } from '@angular/common/http';
import { ListaOperacoesComponent } from './view/lista-operacoes/lista-operacoes.component';
import { FormEntradaComponent } from './view/form-entrada/form-entrada.component';
import { FormSaidaComponent } from './view/form-saida/form-saida.component';
import { ChartsModule } from 'ng2-charts';
import { DashboardOperacoesComponent } from './view/dashboard-operacoes/dashboard-operacoes.component';
import { PaginaInicialComponent } from './view/pagina-inicial/pagina-inicial.component';

@NgModule({
  declarations: [
    AppComponent,
    FullComponent,
    ListaOperacoesComponent,
    FormEntradaComponent,
    FormSaidaComponent,
    DashboardOperacoesComponent,
    PaginaInicialComponent
  ],
  imports: [
    MatMenuModule,
    BrowserModule,
    HttpClientModule,
    DemoMaterialModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(AppRoutes),
    ChartsModule
  ],
  providers: [OperacoesService],
  bootstrap: [AppComponent]
})
export class AppModule {}
