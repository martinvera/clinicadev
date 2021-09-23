import { Component, Input, OnInit } from '@angular/core';
import {
  Chart, registerables
} from 'chart.js';
import { GeneradosOrigen } from 'src/app/shared/model/entity/generadoOrigen';
Chart.register(...registerables);

@Component({
  selector: 'app-chart-donut',
  templateUrl: './chart-donut.component.html',
  styleUrls: ['./chart-donut.component.scss']
})
export class ChartDonutComponent implements OnInit {
  @Input() generadosOrigen: GeneradosOrigen[];
  @Input() header: any;

  total: number = 0;
  labels: string[];
  datos: number[];
  prueba: string;
  color: string[] = [];
  totalPor: number = 0;
  constructor(

  ) {
    this.labels = [];
    this.datos = [];
  }

  ngOnInit(): void {
    this.generadosOrigen.forEach(val => {
      this.total = this.total + val.totalxorigen;
      this.labels.push(val.origen);
    });
    this.generadosOrigen.forEach(val => {
      this.datos.push(((val.totalxorigen / this.total) * 100));
      this.totalPor = this.totalPor + ((val.totalxorigen / this.total) * 100);
    });

    this.initChartDonut();
  }

  initChartDonut() {
    let dataTemp = this.datos;
    let myChart = new Chart("chartDonut", {
      type: 'doughnut',
      data: {
        labels: this.labels,
        datasets: [{
          label: "",
          backgroundColor: ["#52B279", "#2576DE", "#47C2E9", "#47C2E81"],
          data: dataTemp
        }]
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: false,
            text: 'Cálculo de datos por Estado'
          }
        }
      }
    });
  }

}
