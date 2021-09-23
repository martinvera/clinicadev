import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-view',
  templateUrl: './modal-view.component.html',
  styleUrls: ['./modal-view.component.scss']
})
export class ModalViewComponent implements OnInit {

  userItem:any;
  constructor(
    public dialogRef: MatDialogRef <ModalViewComponent>,
    @Inject(MAT_DIALOG_DATA) public dataUser:any
  ) { }

  ngOnInit() {
    this.userItem = this.dataUser;
  }

  closeModal():void{
    this.dialogRef.close(false);
  }


}
