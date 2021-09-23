import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-forgot',
  templateUrl: './modal-forgot.component.html',
  styleUrls: ['./modal-forgot.component.scss']
})
export class ModalForgotComponent {

  form: FormGroup;
  correo: boolean;
  constructor(
    public dialogRef: MatDialogRef<ModalForgotComponent>,
    @Inject(MAT_DIALOG_DATA) public message: string) {
      this.correo = true;
  }
  onClickNO(): void {
    this.dialogRef.close(false);
  }
  createForm() {
    this.form = new FormGroup({
      username: new FormControl(null, Validators.required),
    })
  }

  sentMail() {
    console.log('desde ->')
  }
}
