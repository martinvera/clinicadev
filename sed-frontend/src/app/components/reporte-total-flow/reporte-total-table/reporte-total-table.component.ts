import { Component, OnInit } from '@angular/core';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { ReporteTotalService } from 'src/app/shared/services/clinica-internacional/reporteTotal.service';
import { ENPOINT_REPORTE_TOTAL } from 'src/app/shared/utils/apis';
import { Total } from 'src/app/shared/model/entity/total';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ModalErrorComponent } from '../modal-error/modal-error.component';



export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}

@Component({
  selector: 'app-reporte-total-table',
  templateUrl: './reporte-total-table.component.html',
  styleUrls: ['./reporte-total-table.component.scss']
})
export class ReporteTotalTableComponent implements OnInit {
  displayedColumns: string[] = ['fecha', 'garanteDescripcion', 'weight',];
  dataSource = new MatTableDataSource<Total>();
  permission = Permission;

  dellSuscribe: Subscription = new Subscription();
  form: FormGroup;
  garantes = [{
    "codigo": "RIMAC EPS",
    "descripcion": "RIMAC EPS"
  }, {
    "codigo": "RIMAC SEGUROS",
    "descripcion": "RIMAC SEGUROS"
  }, {
    "codigo": "OTROS GARANTES",
    "descripcion": "OTROS GARANTES"
  }];
  constructor(
    private reporteTotalService: ReporteTotalService,
    private fb: FormBuilder,
    public dialog: MatDialog,
  ) {
    this.form = new FormGroup({
      garante: new FormControl(''),
    });
  }

  ngOnInit(): void {
    this.getReporteTotal();
  }
  getReporteTotal() {

    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_REPORTE_TOTAL.LIST_REPOTE_TOTAL;
    parametro.request = 'POST';
    parametro.data = {
      "header": {
        "transactionId": "",
        "applicationId": "",
        "userId": ""
      },
      "request": {
        "garanteDescripcion": "",
        "tipoReporte": "TOTAL"
      }
    }
    this.dellSuscribe.add(this.reporteTotalService.getReporteTotal(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.dataSource = data.response.data
      }
    ));
  }
  searchReporteTotal() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_REPORTE_TOTAL.SEARCH_REPOTE_TOTAL;
    parametro.request = 'POST';
    parametro.data = {
      "header": {
        "transactionId": "",
        "applicationId": "",
        "userId": ""
      },
      "request": {
        "garanteDescripcion": this.form.value.garante,
        "tipoReporte": "TOTAL"
      }
    }
    this.dellSuscribe.add(this.reporteTotalService.searchReporteTotal(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.dataSource = data.response.data;
      }
    ));
  }

  filtroForm() {
    this.form = this.fb.group({
      garanteId: [null, Validators.required],
      garanteDescripcion: [null, Validators.required],
    });
  }

  clearForm() {
    this.form.reset();
    this.dataSource = new MatTableDataSource([]);
    this.getReporteTotal();

  }
  openDialog(data) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    this.dialog.open(ModalErrorComponent, {
      data: data
    });
  }
}
