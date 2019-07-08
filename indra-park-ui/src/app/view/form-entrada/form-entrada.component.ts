import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { OperacoesService } from '../../services/operacoes.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-form-entrada',
  templateUrl: './form-entrada.component.html',
  styleUrls: ['./form-entrada.component.css']
})
export class FormEntradaComponent implements OnInit {
  modelosVeiculo: string[];
  formEntrada;

  constructor(
    private service: OperacoesService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.modelosVeiculo = ['CARRO', 'MOTO', 'CAMINHAO', 'CAMINHONETE'];
    this.formEntrada = this.formBuilder.group({
      placa: '',
      modelo: ['CARRO']
    });
  }

  ngOnInit() {}

  onSubmit(veiculo) {
    this.service.realizaEntrada(veiculo).subscribe(res => {
      this.router.navigateByUrl("/");
    }, erro => console.log(erro));
  }
}
