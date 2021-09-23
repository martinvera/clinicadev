import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { LoteService } from 'src/app/shared/services/clinica-internacional/lote.service';
import { ENPOINT_LOTE } from 'src/app/shared/utils/apis';

@Component({
  selector: 'app-modal-reproceso',
  templateUrl: './modal-reproceso.component.html',
  styleUrls: ['./modal-reproceso.component.scss']
})
export class ModalReprocesoComponent  {
  
  constructor(
    public dialogRef: MatDialogRef<ModalReprocesoComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private loteService: LoteService,
  ) { }

 
  closeModal(): void {
    this.dialogRef.close(false);
  }

  reprocess(){
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_LOTE.REPROCESAR_LOTE;
    parametro.request = 'POST';
    parametro.data = {
      "request":{
        "nroLote": this.data.rowKey,
        "garanteId": this.data.partitionKey
    }
  }
    this.loteService.reprocesarLote(parametro).subscribe(
      value => {
        this.dialogRef.close(value);
      }
    )
  }

  }





 


