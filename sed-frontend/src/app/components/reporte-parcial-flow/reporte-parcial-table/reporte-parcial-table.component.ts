import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter } from '@angular/material-moment-adapter';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepicker } from '@angular/material/datepicker';
import * as _moment from 'moment';
import { default as _rollupMoment, Moment } from 'moment';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Subscription } from 'rxjs';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { ReporteParcialService } from 'src/app/shared/services/clinica-internacional/reporteParcial.service';
import { ENPOINT_REPORTE_PARCIAL } from 'src/app/shared/utils/apis';
import { Parcial } from 'src/app/shared/model/entity/parcial';
import { MatTableDataSource } from '@angular/material/table';
import { Garante } from 'src/app/shared/model/common/garante';
import { ToastrService } from 'ngx-toastr';
import { DatePipe } from '@angular/common';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ErrorModalParcialComponent } from '../error-modal-parcial/error-modal-parcial.component';

const moment = _rollupMoment || _moment;

export const MY_FORMATS = {
  parse: {
    dateInput: 'MM/YYYY',
  },
  display: {
    dateInput: 'MM/YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};


@Component({
  selector: 'app-reporte-parcial-table',
  templateUrl: './reporte-parcial-table.component.html',
  styleUrls: ['./reporte-parcial-table.component.scss'],
  providers: [
    // `MomentDateAdapter` can be automatically provided by importing `MomentDateModule` in your
    // application's root module. We provide it at the component level here, due to limitations of
    // our example generation script.
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },

    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
  ],
})



export class ReporteParcialTableComponent implements OnInit {
  displayedColumns: string[] = ['fecha', 'usuario', 'filtros', 'estado', 'opcion'];
  dataSource = new MatTableDataSource<Parcial>();
  date = new FormControl();
  dellSuscribe: Subscription = new Subscription();
  chosenYearHandler(normalizedYear: Moment) {
    const ctrlValue = moment()
    ctrlValue.year(normalizedYear.year());
    this.date.setValue(ctrlValue);
    this.form.get('date')?.setValue(ctrlValue);
  }

  garantes: Garante[];
  chosenMonthHandler(normalizedMonth: Moment, datepicker: MatDatepicker<Moment>) {
    const ctrlValue = this.date.value;
    ctrlValue.month(normalizedMonth.month());
    this.date.setValue(ctrlValue);
    datepicker.close();
  }
  permission = Permission;

  form: FormGroup;
  constructor(
    private reporteParcialService: ReporteParcialService,
    private catalogoService: CatalogoService,
    private toastrService: ToastrService,
    private pipe: DatePipe,
    public dialog: MatDialog,
  ) {

    this.form = new FormGroup({
      date: new FormControl('', Validators.required),
      garante: new FormControl('', Validators.required),
      garanteId: new FormControl('', Validators.required),
      lote: new FormControl('', Validators.required),
    });
  }

  ngOnInit(): void {
    this.getListGarante();
    this.getReporteParcial();
  }
  getReporteParcial() {
    var cuerpo: any;
    cuerpo = {
      "request": {
        "tipoReporte": "PARCIAL"
      }
    }
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_REPORTE_PARCIAL.LIST_REPORTE_PARCIAL;
    parametro.request = 'POST';
    parametro.data = cuerpo;

    this.dellSuscribe.add(this.reporteParcialService.getReporteParcial(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.dataSource = data.response.data;
      }
    ));
  }
  getListGarante() {

    this.catalogoService.listaGarante().subscribe(
      value => {
        this.garantes = value.response.data
      }
    )
  }
  setGarante(event) {
    let tempGarante: any = this.garantes;
    let data = tempGarante.find(value => value.codigo == event.value)
    this.form.controls.garante.setValue(data.descripcion);

  }
  searchReporteParcial() {
    let mesAno;
    const date_tem = new Date(this.date.value);
    date_tem.setDate(date_tem.getDate());
    mesAno = this.pipe.transform(date_tem, 'MM-yyyy');
    let data = this.form.value;
    var cuerpo: any;
    cuerpo = {
      "header": {
        "transactionId": "",
        "applicationId": "",
        "userId": ""
      },
      "request": {
        mesanio: mesAno,
        garanteDescripcion: data.garante,
        garanteId: data.garanteId,
        nroLote: data.lote,
        tipoReporte: "PARCIAL"
      }
    }
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_REPORTE_PARCIAL.GENERATE_REPORTE_PARCIAL
    parametro.request = 'POST';
    parametro.data = cuerpo;

    this.dellSuscribe.add(this.reporteParcialService.getReporteParcial(parametro).pipe(
    ).subscribe(
      (data: any) => {
        if (data.response.code == 200) {
          this.getReporteParcial();
          this.toastrService.success(data.response.data);
          this.form.reset();
        }
      }
    ));
  }
  openDialog(data) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    this.dialog.open(ErrorModalParcialComponent, {
      data: data
    });
  }
}
