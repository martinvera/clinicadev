import { Route } from '@angular/compiler/src/core';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';

@Component({
  selector: 'app-modal-forgot',
  templateUrl: './modal-forgot.component.html',
  styleUrls: ['./modal-forgot.component.scss']
})
export class ModalForgotComponent implements OnInit {

  form: FormGroup;
  correo: boolean;
  constructor(
    public dialogRef: MatDialogRef<ModalForgotComponent>,
    @Inject(MAT_DIALOG_DATA) public message: string) {
    this.correo = true;
  }
  ngOnInit(): void {
    this.createForm();
  }
  onClickNO(): void {
    this.dialogRef.close(false);
  }
  createForm() {
    this.form = new FormGroup({
      email: new FormControl(null, [Validators.required, Validators.email]),
    })
  }

  sentMail() {
    this.correo = false;
  }

  closeEmail() {
    this.dialogRef.close(false);
  }
}
