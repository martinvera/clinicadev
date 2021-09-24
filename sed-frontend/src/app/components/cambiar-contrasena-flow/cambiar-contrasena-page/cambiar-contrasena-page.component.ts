import { Component, OnInit } from '@angular/core';
import { ModalCambioConfirmacionComponent } from '../modal-cambio-confirmacion/modal-cambio-confirmacion.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { validarQueSeanIguales } from 'src/app/shared/services/security/app.validator';


@Component({
  selector: 'app-cambiar-contrasena-page',
  templateUrl: './cambiar-contrasena-page.component.html',
  styleUrls: ['./cambiar-contrasena-page.component.scss']
})
export class CambiarContrasenaPageComponent implements OnInit {
  currentPassword = true;
  newPassword = true;
  confirmNewPassword = true;

  form: FormGroup;

  constructor(
    public dialog: MatDialog,

  ) { }

  ngOnInit(): void {
    this.createForm();
  }

  openDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80%";
    this.dialog.open(ModalCambioConfirmacionComponent, {
      data: this.form.value
    });
  }
  createForm() {
    this.form = new FormGroup({
      currentPass: new FormControl('', Validators.required),
      newPass: new FormControl('', Validators.required),
      confirmPass: new FormControl('', Validators.required),
    }, {
      validators: validarQueSeanIguales
    });
  }
  checarSiSonIguales(): boolean {
    return this.form.hasError('noSonIguales') &&
      this.form.get('newPass').dirty &&
      this.form.get('confirmPass').dirty;
  }

}
