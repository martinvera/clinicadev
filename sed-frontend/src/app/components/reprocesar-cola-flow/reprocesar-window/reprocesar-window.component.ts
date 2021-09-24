import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ReprocesarColaPageComponent } from '../reprocesar-cola-page/reprocesar-cola-page.component';

@Component({
  selector: 'app-reprocesar-window',
  templateUrl: './reprocesar-window.component.html',
  styleUrls: ['./reprocesar-window.component.scss']
})
export class ReprocesarWindowComponent implements OnInit {

  constructor(
    public dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.openDialog()
  }
  openDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80%";
    this.dialog.open(ReprocesarColaPageComponent, {
      disableClose: true
    }).afterClosed().subscribe(async (data1: any) => {

    });
  }
  redirect() {
    console.log('kil')
  }
}
