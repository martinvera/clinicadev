import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Validators, FormGroup, FormControl } from '@angular/forms';
import { environment } from 'src/environments/environment';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { LoginService } from 'src/app/shared/services/security/login.service';
import { LOGIN_APIS } from 'src/app/shared/utils/apis';
import { AuthService } from 'src/app/shared/services/security/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { ModalForgotComponent } from '../modal-forgot/modal-forgot.component';
import { ModalComponent } from 'src/app/shared/components/modal/modal.component';
import jwt_decode from "jwt-decode";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  form: FormGroup;
  formError: boolean = false;
  llaveWeb: string;
  constructor(
    public router: Router,
    private _login: LoginService,
    private _authServ: AuthService,
    public dialog: MatDialog
  ) {
    // this.llaveWeb = environment.llaveWeb;
    this.form = new FormGroup({})
  }
  ngOnInit() {
    this.createForm();
    localStorage.clear();
  }


  createForm() {
    this.form = new FormGroup({
      username: new FormControl(null, Validators.required),
      password: new FormControl(null, Validators.required),
      // recaptchaReactive: new FormControl('', Validators.required),
      // grant_type: new FormControl('password'),
    })
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(ModalComponent, {
      width: '400px',
      panelClass: 'dialog-content-example',
    });
    dialogRef.afterClosed().subscribe();
  }


  openDialogPass(): void {
    const dialogPass = this.dialog.open(ModalForgotComponent, {
      width: '600px',
      panelClass: 'dialog-content-example',
    });
    dialogPass.afterClosed().subscribe();
  }

  isLogin() {
    let user = this.form.value;
    const parametro: Parameter = new Parameter();
    parametro.url = LOGIN_APIS.SIGNIN;
    parametro.request = 'POST';
    var data = new FormData();
    data.append("username", user.username);
    data.append("password", user.password);
    data.append("grant_type", "password");
    parametro.data = data;
    this._login.login(parametro).subscribe({
      next: value => {
        let user_name: any = jwt_decode(value.body.access_token);
        this.formError = false;
        this._authServ.AuthLoginToken(value.body.access_token);
        this._authServ.userName(user_name.user_name);
      },
      error: err => {
        this.formError = true;
        this.openDialog();
      },
    });
  }

  forgotPass() {
    this.openDialogPass();
  }



}
