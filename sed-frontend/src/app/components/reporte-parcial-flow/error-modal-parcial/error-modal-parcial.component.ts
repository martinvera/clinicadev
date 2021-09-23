import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-error-modal-parcial',
  templateUrl: './error-modal-parcial.component.html',
  styleUrls: ['./error-modal-parcial.component.scss']
})
export class ErrorModalParcialComponent implements OnInit {
  userItem: any;
  dataSource: any;
  constructor(
    public dialogRef: MatDialogRef<ErrorModalParcialComponent>,
    @Inject(MAT_DIALOG_DATA) public error: any,
  ) { }

  ngOnInit(): void {
    this.error = this.error.mensajeError;
  }

  closeModal(): void {
    this.dialogRef.close(false);
  }
}
