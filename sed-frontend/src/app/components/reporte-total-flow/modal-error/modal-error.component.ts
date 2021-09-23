import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-error',
  templateUrl: './modal-error.component.html',
  styleUrls: ['./modal-error.component.scss']
})
export class ModalErrorComponent implements OnInit {
  userItem: any;
  dataSource: any;
  constructor(
    public dialogRef: MatDialogRef<ModalErrorComponent>,
    @Inject(MAT_DIALOG_DATA) public error: any,
  ) { }

  ngOnInit(): void {
    this.error = this.error.mensajeError;
  }

  closeModal(): void {
    this.dialogRef.close(false);
  }
}
