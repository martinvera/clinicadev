import { Component, Input, OnInit } from '@angular/core';

import {
  Chart, registerables
} from 'chart.js';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { MecanismoModoFact } from 'src/app/shared/model/entity/mecanismoModo';
import { MecanismoService } from 'src/app/shared/services/clinica-internacional/dashboard_mecanismo.service';
import { ENPOINT_REPORTE } from 'src/app/shared/utils/apis';
Chart.register(...registerables);

@Component({
  selector: 'app-chart-mecanismo',
  templateUrl: './chart-mecanismo.component.html',
  styleUrls: ['./chart-mecanismo.component.scss']
})
export class ChartMecanismoComponent implements OnInit {
  permission = Permission;
  dellSuscribe: Subscription = new Subscription();
  @Input() mecanismoModoFact: MecanismoModoFact[];
  @Input() header: any;
  cantFacturas: number[];
  labels: string[];
  datos: number[];
  color1: string[] = [];
  color2: string[] = [];
  importe: number[];
  constructor(
    private dashboardMecanismo: MecanismoService,
    private toastrService: ToastrService,
  ) {
    this.cantFacturas = [];
    this.importe = [];
    this.labels = [];
  }

  ngOnInit(): void {
    this.mecanismoModoFact.forEach(val => {
      this.labels.push(val.mecanismoFacturacionDesc + ' - ' + val.modoFacturacion);
      this.cantFacturas.push(val.cantidad);
      this.importe.push(val.importe);
    });
    this.initHorizontalBar1();
    this.initHorizontalBar2();
  }
  initHorizontalBar1() {
    let dataTemp = this.cantFacturas;
    let myChart = new Chart("bars", {
      type: 'bar',
      data: {
        labels: this.labels,
        datasets: [{
          label: "",
          backgroundColor: '#00A1E1',
          data: dataTemp,
          borderWidth: 1
        }]
      },
      options: {
        indexAxis: 'y',
        elements: {
          bar: {
            borderWidth: 2,
          }
        },
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'right',
            display: false
          },
          title: {
            display: true,
            text: ''
          },
        }
      },
    }
    );

    
  }
  initHorizontalBar2() {
    let dataTemp = this.importe;
    let myChart = new Chart("bars2", {
      type: 'bar',
      data: {
        labels: this.labels,
        datasets: [{
          label: "",
          backgroundColor: '#D45758',
          data: dataTemp,
          borderWidth: 1
        }]
      },
      options: {
        indexAxis: 'y',
        elements: {
          bar: {
            borderWidth: 2,
          }
        },
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'right',
            display: false
          },
          title: {
            display: true,
            text: ''
          }
        }
      },
    }
    );
  }
  GENERAR_REPORTE_MECANISMO() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_REPORTE.GENERAR_REPORTE;
    parametro.request = 'POST';
    parametro.data = {
      "request": {
        "fechaDesde": this.header.fechaDesde,
        "fechaHasta": this.header.fechaHasta,
        "tipoReporte": "EXPEDIENTEMECANISMO",
        "mecanismoFacturacionId": this.header.mecanismoFacturacionId
      }
    }
    this.header = parametro.data.request;
    this.dellSuscribe.add(this.dashboardMecanismo.generarReporte(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.toastrService.success('Se envío a generar el reporte.');
      }
    ));
  }
}


