import { Component, OnInit } from '@angular/core';
import { OperacoesService } from 'src/app/services/operacoes.service';

@Component({
  selector: 'app-pagina-inicial',
  templateUrl: './pagina-inicial.component.html',
  styleUrls: ['./pagina-inicial.component.css']
})
export class PaginaInicialComponent implements OnInit {
  operacoes: any;

  constructor(private service: OperacoesService) {}

  ngOnInit() {
    this.service
      .getOperacoesDoDia()
      .subscribe(res => (this.operacoes = res), error => console.log(error));
  }

  onFiltro(filtros) {
    if (filtros.dataInicial && filtros.dataFinal) {
      this.service
        .filtraPorData(filtros.dataInicial, filtros.dataFinal)
        .subscribe(res => (this.operacoes = res));
    } else if (filtros.placa) {
      this.service
        .getOperacoesDeUmVeiculo(filtros.placa)
        .subscribe(res => (this.operacoes = res));
    }
  }
}
