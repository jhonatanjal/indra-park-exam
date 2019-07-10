import { Component, OnInit } from '@angular/core';
import { ChartDataSets, ChartOptions } from 'chart.js';
import { Color, Label } from 'ng2-charts';
import { OperacoesService } from 'src/app/services/operacoes.service';

@Component({
  selector: 'app-dashboard-operacoes',
  templateUrl: './dashboard-operacoes.component.html',
  styleUrls: ['./dashboard-operacoes.component.css']
})
export class DashboardOperacoesComponent implements OnInit {
  chartOptions: ChartOptions = {
    responsive: true,
    title: {
      display: true,
      text: 'Operações da semana'
    },
    tooltips: {
      mode: 'index',
      intersect: false
    }
  };

  private qtdsCarro = [];
  private qtdsMoto = [];
  private qtdsCaminhao = [];
  private qtdsCaminhonete = [];

  chartData: ChartDataSets[] = [
    { data: this.qtdsCarro, label: 'Carro' },
    { data: this.qtdsMoto, label: 'Moto' },
    { data: this.qtdsCaminhao, label: 'Caminhão' },
    { data: this.qtdsCaminhonete, label: 'Caminhonete' }
  ];

  chartLabels: Label[] = [];

  public lineChartColors: Color[] = [
    {
      // red
      backgroundColor: 'rgba(148,159,177,0.0)',
      borderColor: 'red',
      pointBackgroundColor: 'red',
      pointBorderColor: 'red',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    },
    {
      // blue
      backgroundColor: 'rgba(77,83,96,0.0)',
      borderColor: 'blue',
      pointBackgroundColor: 'blue',
      pointBorderColor: 'blue',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(77,83,96,1)'
    },
    {
      // yellow
      backgroundColor: 'rgba(255,0,0,0.0)',
      borderColor: 'yellow',
      pointBackgroundColor: 'yellow',
      pointBorderColor: 'yellow',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    },
    {
      // green
      backgroundColor: 'rgba(255,0,0,0.0)',
      borderColor: 'green',
      pointBackgroundColor: 'green',
      pointBorderColor: 'green',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    }
  ];

  constructor(private service: OperacoesService) {}

  ngOnInit() {
    this.service
      .getEstatisticasDaSemana()
      .subscribe(res => this.preencheGrafico(res));
  }

  preencheGrafico(dados) {
    let dias = Object.keys(dados);
    this.chartLabels = dias.sort((a, b) => {
      let anoAtual = new Date().getFullYear();
      a = a + '/' + anoAtual;
      b = b + '/' + anoAtual;
      
      let data1 = new Date(a);
      let data2 = new Date(b);
      
      if (data1 > data2) return 1;
      if (data1 < data2) return -1;
      return 0;
    });

    dias.forEach((dia, index) => {
      let modelos = Object.keys(dados[dia]);

      this.qtdsCarro.push(0);
      this.qtdsMoto.push(0);
      this.qtdsCaminhao.push(0);
      this.qtdsCaminhonete.push(0);

      modelos.forEach(modelo => {
        switch (modelo) {
          case 'CARRO':
            this.qtdsCarro[index] += dados[dia][modelo];
            break;
          case 'MOTO':
            this.qtdsMoto[index] += dados[dia][modelo];
            break;
          case 'CAMINHAO':
            this.qtdsCaminhao[index] += dados[dia][modelo];
            break;
          case 'CAMINHONETE':
            this.qtdsCaminhonete[index] += dados[dia][modelo];
            break;
        }
      });
    });
  }
}
