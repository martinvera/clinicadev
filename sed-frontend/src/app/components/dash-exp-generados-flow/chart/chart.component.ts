import { Component, Input, OnInit } from '@angular/core';
import {
  Chart, registerables
} from 'chart.js';
import { GeneradosEstado } from 'src/app/shared/model/entity/generadoEstado';
Chart.register(...registerables);

@Component({
  selector: 'app-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit {
  @Input() generadosEstadoList: GeneradosEstado[];
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
    this.generadosEstadoList.forEach(val => {
      this.total = this.total + val.totalxestado;
      this.labels.push(val.estado);
    });
    this.generadosEstadoList.forEach(val => {
      this.datos.push(((val.totalxestado / this.total) * 100));
      this.totalPor = this.totalPor + ((val.totalxestado / this.total) * 100);
    });
    this.initChartCake();
  }

  initChartCake() {
    let dataTemp = this.datos;
    let myChart = new Chart("chartCake", {
      type: 'pie',
      data: {
        labels: this.labels,
        datasets: [{
          label: "Population (millions)",
           backgroundColor: ["#2576DE", "#52B279", "#47C2E9", "#47C2E81"],
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
