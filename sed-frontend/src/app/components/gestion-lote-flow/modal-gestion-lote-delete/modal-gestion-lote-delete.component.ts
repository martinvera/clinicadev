import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { LoteService } from 'src/app/shared/services/clinica-internacional/lote.service';
import { ENPOINT_LOTE } from 'src/app/shared/utils/apis';

@Component({
  selector: 'app-modal-gestion-lote-delete',
  templateUrl: './modal-gestion-lote-delete.component.html',
  styleUrls: ['./modal-gestion-lote-delete.component.scss']
})
export class ModalGestionLoteDeleteComponent  {


  constructor(

    public dialogRef: MatDialogRef<ModalGestionLoteDeleteComponent>,
    private loteService: LoteService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

 

  closeModal(): void {
    this.dialogRef.close(false);
  }

  isDelete() {

    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_LOTE.DELETE_LOTE;
    parametro.request = 'POST';
    parametro.data = {
      "request": {
          "nroLote": this.data.rowKey,
          "garanteId": this.data.partitionKey
      }
  }
    this.loteService.deleteLote(parametro).subscribe(
      value => {
        this.dialogRef.close(value);
      }
    )
  }

}
