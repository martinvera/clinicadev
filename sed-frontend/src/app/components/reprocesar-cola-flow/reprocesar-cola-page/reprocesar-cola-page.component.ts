import { DatePipe } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { ReporteService } from 'src/app/shared/services/clinica-internacional/reporte.service';
import { ENPOINT_REPORCESAR, ENPOINT_REPORTE_SINLOTE } from 'src/app/shared/utils/apis';

@Component({
  selector: 'app-reprocesar-cola-page',
  templateUrl: './reprocesar-cola-page.component.html',
  styleUrls: ['./reprocesar-cola-page.component.scss']
})
export class ReprocesarColaPageComponent implements OnInit {
  form: FormGroup;
  dellSuscribe: Subscription = new Subscription();

  constructor(
    public dialogRef: MatDialogRef<ReprocesarColaPageComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private pipe: DatePipe,
    private reporteService: ReporteService,
    private alertService: ToastrService,
    private router: Router,
  ) { }

  ngOnInit(): void {
  }

  closeModal(): void {
    this.dialogRef.close(false);
  }

  procesarFacturas() {

    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_REPORCESAR.REPROCESAR_FACTURAS;
    parametro.data = {
      "header": {
        "transactionId": "",
        "applicationId": "",
        "userId": ""
      },
      "request": ""
    }
    parametro.request = 'POST';
    this.dellSuscribe.add(this.reporteService.setReportes(parametro).pipe(
    ).subscribe(
      (value: any) => {
        if (value.response.code == 200 ) {
          this.dialogRef.close(false);
          this.alertService.success(value.response.data);
          this.router.navigate(["/"])
        }
      }
    ));
  }

  procesarEncuentros() {

    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_REPORCESAR.REPROCESAR_ENCUENTRO;
    parametro.data = {
      "header": {
        "transactionId": "",
        "applicationId": "",
        "userId": ""
      },
      "request": ""
    }
    parametro.request = 'POST';
    this.dellSuscribe.add(this.reporteService.setReportes(parametro).pipe(
    ).subscribe(
      (value: any) => {
        if (value.response.code == 200 ) {
          this.dialogRef.close(false);
          this.alertService.success(value.response.data);
          this.router.navigate(["/"])
        }
      }
    ));
  }

  close() {
    this.dialogRef.close(false);
    this.router.navigate(["/"])
  }

}
