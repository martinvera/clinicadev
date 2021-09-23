import { Component, Inject, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-error-documentos-modal',
  templateUrl: './error-documentos-modal.component.html',
  styleUrls: ['./error-documentos-modal.component.scss']
})
export class ErrorDocumentosModalComponent implements OnInit {
  error:string = "";
  constructor(
    public dialogRef: MatDialogRef<ErrorDocumentosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.error = this.data.msjError;
  }
  closeModal(): void {
    this.dialogRef.close(false);
  }
}
