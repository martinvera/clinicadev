import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Documento } from 'src/app/shared/model/entity/document';
import { ErrorDocumentosModalComponent } from '../error-documentos-modal/error-documentos-modal.component';

@Component({
  selector: 'app-documentos-modal',
  templateUrl: './documentos-modal.component.html',
  styleUrls: ['./documentos-modal.component.scss']
})
export class DocumentosModalComponent implements OnInit {

  displayedColumns: string[] = ['Encuentro', 'Fecha creación', 'Estado', 'Tipo documento', 'Detalle'];

  dataSource: MatTableDataSource<Documento>;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  constructor(
    public dialogRef: MatDialogRef<DocumentosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public dataEncuentro: any,
    public dialog: MatDialog,
  ) {

    this.dataSource = new MatTableDataSource();
  }

  ngOnInit() {
    this.dataSource = new MatTableDataSource(this.dataEncuentro.documento);
  }

  openDialog(data: any): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "100%";
    this.dialog.open(ErrorDocumentosModalComponent, {
      data: {
        data,
        documento: data
      }
    });

  }
}
