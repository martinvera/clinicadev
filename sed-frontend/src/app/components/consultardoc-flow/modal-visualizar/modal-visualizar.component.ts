import { Component, OnInit,Inject } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-visualizar',
  templateUrl: './modal-visualizar.component.html',
  styleUrls: ['./modal-visualizar.component.scss']
})
export class ModalVisualizarComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ModalVisualizarComponent>,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public message: string
    ) { }

    Json_item:any;

  ngOnInit() {
    this.setData();
  }

  setData(){
    this.Json_item = this.message;
  }

  onClickNO():void{
    this.dialogRef.close(false);
  }

  openViewDocument(item:any):void{
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80%";
    
  }

  openViewPdf(){
    this.openViewDocument(null);
  }

}
