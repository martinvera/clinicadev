import { Component, Inject, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { EnviadoGarante } from 'src/app/shared/services/clinica-internacional/enviado_garante.service';
import { ENPOINT_ENVIADOS_GARANTE } from 'src/app/shared/utils/apis';
import { LotesEnviadosModalComponent } from '../lotes-enviados-modal/lotes-enviados-modal.component';


export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}
export interface Historial {
  estado: string;
  etag: string;
  fechaAceptacion: string;
  fechaEnvio: string;
  fechaLote: string;
  garanteId: string;
  nroLote: number;
  partitionKey: string
  rowKey: number;
  timestamp: number;
  userName: string;
}

@Component({
  selector: 'app-lotes-enviados-log',
  templateUrl: './lotes-enviados-log.component.html',
  styleUrls: ['./lotes-enviados-log.component.scss']
})
export class LotesEnviadosLogComponent implements OnInit {
  displayedColumns: string[] = ['position', 'lote', 'name', 'weight', 'symbol', 'aceptacion','rechazo', 'registroSolicitud','user'];

  dataSource = new MatTableDataSource<Historial>();
  constructor(
    public dialogRef: MatDialogRef<LotesEnviadosModalComponent>,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private enviadoGaranteService: EnviadoGarante,
  ) { }

  ngOnInit(): void {
    this.getLogEnviadoGarante();
  }

  dialogClose() {
    this.dialogRef.close(false);
  }
  getLogEnviadoGarante() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_ENVIADOS_GARANTE.LOG_ENVIADOS_GARANTE;
    parametro.request = 'POST';
    parametro.data = {
      request: {
        garanteId: this.data.partitionKey,
        garanteDescripcion: this.data.garanteDescripcion,
        nroLote: this.data.rowKey,
      }
    }
    this.enviadoGaranteService.getLogEnviadoGarante(parametro).
      subscribe(
        (value: any) => {
          if (value.response.data.length > 0) {
            this.dataSource = value.response.data;
          }
        }
      )
  }
}
