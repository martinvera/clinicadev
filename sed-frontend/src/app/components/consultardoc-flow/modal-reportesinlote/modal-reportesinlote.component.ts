import { ENPOINT_REPORTE_SINLOTE } from './../../../shared/utils/apis';
import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { ToastrService } from 'ngx-toastr';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { Subscription } from 'rxjs';
import { ReporteService } from 'src/app/shared/services/clinica-internacional/reporte.service';


@Component({
  selector: 'app-modal-reportesinlote',
  templateUrl: './modal-reportesinlote.component.html',
  styleUrls: ['./modal-reportesinlote.component.scss']
})
export class ModalReportesinloteComponent implements OnInit {

  form: FormGroup;
  dellSuscribe: Subscription = new Subscription();
  constructor(
    public dialogRef: MatDialogRef<ModalReportesinloteComponent>,
    public dialog: MatDialog,
    private pipe: DatePipe,
    private reporteService: ReporteService,
    private alertService: ToastrService,
  ) {

  }

  ngOnInit(): void {
    this.onCreateFilter();
  }

  onCreateFilter() {
    this.form = new FormGroup({
      fechaDesde: new FormControl('', Validators.required),
      fechaHasta: new FormControl('', Validators.required),
    });
  }

  openEvent(e: MatDatepickerInputEvent<Date>) {
    const fechaHasta = (<HTMLInputElement>document.getElementById('fechaHasta')).value;
    const today = new Date();
    if (fechaHasta == '') {
      this.form.get('fechaHasta')?.setValue(today);
    }
  }

  generarReporteError() {
    let fechaDesde: any;
    let fechaHasta: any;
    if (this.form.value.fechaDesde !== "" && this.form.value.fechaDesde !== null) {
      const date = new Date(this.form.value.fechaDesde);
      date.setDate(date.getDate());
      fechaDesde = this.pipe.transform(date, 'yyyy-MM-dd');
    } else {
      fechaDesde = null;
    }

    if (this.form.value.fechaHasta !== "" && this.form.value.fechaHasta !== null) {
      const date2 = new Date(this.form.value.fechaHasta);
      date2.setDate(date2.getDate());
      fechaHasta = this.pipe.transform(date2, 'yyyy-MM-dd');
    } else {
      fechaHasta = null;
    }

    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_REPORTE_SINLOTE.REPORTE_ENCUENTRO_SINLOTE;
    parametro.data = {
      "request": {
        "fechaDesde": fechaDesde,
        "fechaHasta": fechaHasta,
        "tipoReporte": "ENCUENTROSINLOTE"
      }
    };
    parametro.request = 'POST';
    this.dellSuscribe.add(this.reporteService.setReportes(parametro).pipe(
    ).subscribe(
      (value: any) => {
        if (value.response.code == 200) {
          this.dialogRef.close(false);
          this.alertService.success(value.response.data);
        }
      }
    ));
  }
  clickGenerar() {
    this.dialogRef.close(false);
  }

  clickClose() {
    this.dialogRef.close();
  }
}
