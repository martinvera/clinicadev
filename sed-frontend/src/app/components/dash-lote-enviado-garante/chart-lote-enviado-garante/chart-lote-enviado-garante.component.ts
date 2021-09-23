import { Component, Input, OnInit } from '@angular/core';
import { Chart } from 'chart.js';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Garante } from 'src/app/shared/model/common/garante';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { EnviadosGarante } from 'src/app/shared/model/entity/enviadosGarante';
import { EnviadoGarante } from 'src/app/shared/services/clinica-internacional/enviado_garante.service';
import { ENPOINT_ENVIADOS_GARANTE } from 'src/app/shared/utils/apis';

@Component({
  selector: 'app-chart-lote-enviado-garante',
  templateUrl: './chart-lote-enviado-garante.component.html',
  styleUrls: ['./chart-lote-enviado-garante.component.scss']
})
export class ChartLoteEnviadoGaranteComponent implements OnInit {
  permission = Permission;
  dellSuscribe: Subscription = new Subscription();
  @Input() enviadosGarante: EnviadosGarante[];
  @Input() header: any;
  garante: string;
  fechaDesdeHasta: string;

  total: number = 0;
  labels: string[];
  datos: number[];
  prueba: string;
  color: string[] = [];
  totalPor: number = 0;

  constructor(
    private enviadosGaranteService: EnviadoGarante,
    private toastrService: ToastrService
  ) {
    this.enviadosGarante = [];
    this.labels = [];
    this.datos = [];
   
  }

  ngOnInit(): void {
     this.garante = this.header.garanteDescripcion;
    this.fechaDesdeHasta = this.header.fechaDesdeHasta;
    this.enviadosGarante.forEach(val => {
      this.total = this.total + val.total;
      this.labels.push(val.estadoGarante);
      if (val.estadoGarante == 'PENDIENTE') {
        this.color.push('#d4c42d');
      } else if (val.estadoGarante == 'ENVIADO') {
        this.color.push('#e26f2e');
      } else if (val.estadoGarante == 'ACEPTADO') {
        this.color.push('#249e55');
      } else if (val.estadoGarante == 'RECHAZADO') {
        this.color.push('#c52c2c');
      }
    });
    this.enviadosGarante.forEach(val => {
      this.datos.push(((val.total / this.total) * 100));
      this.totalPor = this.totalPor + ((val.total / this.total) * 100);
    });
    this.initChartCake()
  }
  initChartCake() {
    let dataTemp = this.datos;

    let myChart = new Chart("chartCake", {
      type: 'pie',
      data: {
        labels: this.labels,
        datasets: [{
          label: "",
          backgroundColor: this.color,
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
  reportEnviadosGarante() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_ENVIADOS_GARANTE.REPORTE_ENVIADOS_GARANTE;
    parametro.request = 'POST';
    parametro.data = {
      "request": {
        "fechaDesde": this.header.fechaLoteDesde,
        "fechaHasta": this.header.fechaLoteHasta,
        "tipoReporte": "ENVIADOGARANTE",
        "garanteId": this.header.garanteId,
        estado: 'TERMINADO',
      }
    }
    this.header = parametro.data.request;
    this.dellSuscribe.add(this.enviadosGaranteService.setReportEnviadosGarante(parametro).pipe(
    ).subscribe(
      (data: any) => {
        if (data.response.code == 200 && data.response.data) {
          this.toastrService.success(data.response.data);
        } else {
          this.toastrService.error('Se produjo un error, contactarse con soporte. Gracias.');
        }
      }
    ));
  }
}