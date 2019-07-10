import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OperacoesService } from '../../services/operacoes.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-form-saida',
  templateUrl: './form-saida.component.html',
  styleUrls: ['./form-saida.component.css']
})
export class FormSaidaComponent implements OnInit {
  
  formSaida: FormGroup = this.formBuilder.group({
    placa: ['', [Validators.required, Validators.pattern('[a-zA-Z0-9]{7}')]]
  });
  operacao: any;
  valor;
  valorNaoCalculado: boolean = true;
  erroBuscandoPlaca: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private service: OperacoesService,
    private router: Router,
    private acRouter: ActivatedRoute
  ) {}

  ngOnInit() {
    this.acRouter.params.subscribe(params => {
      if (params.placa) {
        this.formSaida.setValue({ 'placa': params.placa });
        this.buscaOperacaoEmAberto({placa: params.placa});
      }
    });
  }

  get placa() {
    return this.formSaida.get('placa');
  }

  buscaOperacaoEmAberto(value) {
    if (this.formSaida.valid) {
      this.service.getOperacaoDeEntradaDoVeiculo(value.placa).subscribe(
        res => {
          this.operacao = res;
          this.erroBuscandoPlaca = false;
        },
        error => {
          console.log(error);
          this.erroBuscandoPlaca = true;
        }
      );
    }
  }

  calculaValor() {
    this.service.getValorEstacionamento(this.operacao.id).subscribe(res => {
      this.valor = res;
      this.valorNaoCalculado = false;
    });
  }

  finalizaOpeacao() {
    this.service
      .finalizaOperacao(this.operacao.id)
      .subscribe(
        () => this.router.navigateByUrl('/'),
        erro => console.log(erro)
      );
  }
}
