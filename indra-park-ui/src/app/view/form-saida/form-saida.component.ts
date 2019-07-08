import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { OperacoesService } from '../../services/operacoes.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-form-saida',
  templateUrl: './form-saida.component.html',
  styleUrls: ['./form-saida.component.css']
})
export class FormSaidaComponent implements OnInit {
  formSaida: FormGroup = this.formBuilder.group({ placa: [''] });
  operacao: any = null;
  valor: any;

  constructor(
    private formBuilder: FormBuilder,
    private operacaoService: OperacoesService,
    private router: Router,
    private acRouter: ActivatedRoute
  ) {}

  ngOnInit() {
    this.acRouter.params.subscribe(params => {
      this.buscaOperacaoEmAberto(params);
    });
  }

  buscaOperacaoEmAberto(value) {
    if (value.placa) {
      this.operacaoService
        .getOperacaoDeEntradaDoVeiculo(value.placa)
        .subscribe(res => (this.operacao = res));
    }
  }

  calculaValor() {
    this.operacaoService
      .getValorEstacionamento(this.operacao.id)
      .subscribe(res => {
        this.valor = res;
      });
  }

  finalizaOpeacao(placa: string) {
    this.operacaoService
      .finalizaOperacao(this.operacao.placa)
      .subscribe(
        () => this.router.navigateByUrl('/'),
        erro => console.log(erro)
      );
  }
}
