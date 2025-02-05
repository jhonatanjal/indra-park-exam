import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-lista-operacoes',
  templateUrl: './lista-operacoes.component.html',
  styleUrls: ['./lista-operacoes.component.css']
})
export class ListaOperacoesComponent implements OnInit {

  @Input() operacoes;
  @Output() filtro = new EventEmitter();
  @Output() reset = new EventEmitter();
  filtroAtivo: boolean = false;

  formFiltro = this.fb.group({
    placa: [''],
    dataInicial: [''],
    dataFinal: ['']
  })

  displayedColumns: string[] = ['placa', 'modelo', 'dataHora', 'estado'];

  constructor(
    private fb: FormBuilder,
    private router: Router,
  ) { }

  ngOnInit() {
  }

  filtraOperacoes(filtros) {
    this.filtro.emit(filtros);
    this.filtroAtivo = true;
  }

  limpaFiltros() {
    this.filtroAtivo = false;
    this.reset.emit();
    this.formFiltro.reset();
  }

  vaiParaFormSaida(placa) {
    this.router.navigate(['saida', {placa}])
  }

}
