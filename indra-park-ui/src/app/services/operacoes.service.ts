import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class OperacoesService {
  
  url: string = 'http://localhost:8080/operacoes/';

  constructor(private http: HttpClient) {}

  getOperacoesDoDia() {
    return this.http.get(this.url, httpOptions);
  }

  filtraPorData(dataInicial, dataFinal) {
    return this.http.get(
      this.url + `?dataInicial=${dataInicial}&dataFinal=${dataFinal}`,
      httpOptions
    );
  }

  getOperacoesDeUmVeiculo(placa) {
    return this.http.get(this.url + `?veiculoPlaca=${placa}`, httpOptions);
  }

  realizaEntrada(veiculo) {
    return this.http.post(this.url, veiculo, httpOptions);
  }

  getOperacaoDeEntradaDoVeiculo(placa) {
    return this.http.get(this.url + `entrada?veiculoPlaca=${placa}`, httpOptions);
  }

  getValorEstacionamento(operacaoId: number) {
    return this.http.get(this.url + `valorCobrado?operacaoId=${operacaoId}`, httpOptions);
  }

  finalizaOperacao(operacaoId: number) {
    return this.http.put(this.url + `${operacaoId}`, httpOptions);
  }

  getEstatisticasDaSemana() {
    return this.http.get(this.url + 'estatisticas', httpOptions);
  }
}
