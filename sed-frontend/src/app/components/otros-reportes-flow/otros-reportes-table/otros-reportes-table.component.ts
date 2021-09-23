import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { OtrosReportesService } from 'src/app/shared/services/clinica-internacional/otrosreportes.service';
import { ENPOINT_OTROSREPORTES } from 'src/app/shared/utils/apis';
import { ModalMensajeErrorComponent } from '../modal-mensaje-error/modal-mensaje-error.component';

export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}

@Component({
  selector: 'app-otros-reportes-table',
  templateUrl: './otros-reportes-table.component.html',
  styleUrls: ['./otros-reportes-table.component.scss']
})
export class OtrosReportesTableComponent implements OnInit {
  displayedColumns: string[] = ['position', 'name', 'symbol', 'estado', 'opcion'];
  permission = Permission;
  dataSource: any;
  constructor(
    private otrosReportesService: OtrosReportesService,
    public dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.getListOtrosReportes();
  }
  getListOtrosReportes() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_OTROSREPORTES.OTROS_REPORTES;
    parametro.request = 'POST';
    parametro.data = {
      "header": {
        "transactionId": "",
        "applicationId": "",
        "userId": ""
      },
      "request":{
        "tipoReporte":"OTROS",
        }
    }
    this.otrosReportesService.getOtrosReportes(parametro).subscribe(
      value => {
        this.dataSource = value.response.data
      }
    )
  }

  openDialog(data) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    this.dialog.open(ModalMensajeErrorComponent, {
      data: data
    });
  }

}
