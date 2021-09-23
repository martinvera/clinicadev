
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { ReporteService } from 'src/app/shared/services/clinica-internacional/reporte.service';
import { ENPOINT_REPORTE } from 'src/app/shared/utils/apis';

@Component({
  selector: 'app-modal-reporte',
  templateUrl: './modal-reporte.component.html',
  styleUrls: ['./modal-reporte.component.scss']
})
export class ModalReporteComponent implements OnInit {
  form: FormGroup;
  dellSuscribe: Subscription = new Subscription();
  constructor(
    public dialogRef: MatDialogRef<ModalReporteComponent>,
    public dialog: MatDialog,
    private reporteService: ReporteService,
    private alertService: ToastrService,
    private catalogoService: CatalogoService
  ) { }

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
    

    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_REPORTE.GENERAR_REPORTE;
    parametro.data = {
      "request": {
        "fechaDesde": this.form.value.fechaDesde !== "" && this.form.value.fechaDesde !== null ? this.catalogoService.formaDate(this.form.value.fechaDesde): null,
        "fechaHasta": this.form.value.fechaHasta !== "" && this.form.value.fechaHasta !== null ? this.catalogoService.formaDate(this.form.value.fechaHasta) : null,
        "tipoReporte": "EXPEDIENTECONERROR"
      }
    };
    parametro.request = 'POST';
    this.dellSuscribe.add(this.reporteService.setReportes(parametro).pipe(
    ).subscribe(
      (value: any) => {
        if (value.response.code == 200 && value.response.data == 'Enviado a cola correctamente') {
          this.dialogRef.close(false);
          this.alertService.success(value.response.data);
        }

      }
    ));
  }
  clickClose(){
    this.dialogRef.close();
  }
}
