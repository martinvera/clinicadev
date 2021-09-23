import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-validate',
  templateUrl: './modal-validate.component.html',
  styleUrls: ['./modal-validate.component.scss']
})
export class ModalValidateComponent  {

  constructor(
    public dialogRef: MatDialogRef<ModalValidateComponent>,
  ) { }

  
  onClickNO(): void {
    this.dialogRef.close(false);
  }

}
