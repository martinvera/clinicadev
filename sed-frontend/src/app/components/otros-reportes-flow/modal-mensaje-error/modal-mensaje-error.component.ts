import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-mensaje-error',
  templateUrl: './modal-mensaje-error.component.html',
  styleUrls: ['./modal-mensaje-error.component.scss']
})
export class ModalMensajeErrorComponent implements OnInit {

  userItem: any;
  dataSource: any;
  constructor(
    public dialogRef: MatDialogRef<ModalMensajeErrorComponent>,
    @Inject(MAT_DIALOG_DATA) public error: any,
  ) { }

  ngOnInit() {
    this.error = this.error.mensajeError;
  }

  closeModal(): void {
    this.dialogRef.close(false);
  }
}
