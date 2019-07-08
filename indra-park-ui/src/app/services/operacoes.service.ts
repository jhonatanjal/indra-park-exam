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

  private getHeaders(): HttpHeaders {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');
    return headers;
  }

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
    return this.http.get(this.url + `/${placa}`, httpOptions);
  }
  
  realizaEntrada(veiculo) {
    return this.http.post(this.url, veiculo, httpOptions);
  }

  getOperacaoDeEntradaDoVeiculo(placa) {
    return this.http.get(this.url + `/${placa}/entrada`, httpOptions);
  }
  
  getValorEstacionamento(operacaoId) {
    return this.http.get(this.url + `/${operacaoId}/valorCobrado`, httpOptions);
  }

  finalizaOperacao(placa: string) {
    return this.http.put(this.url + `/${placa}`, httpOptions);
  }

  getEstatisticasDaSemana() {
    return this.http.get(this.url + '/estatisticas', httpOptions);
  }
}
