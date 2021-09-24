import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { LoginService } from 'src/app/shared/services/security/login.service';
import { ENDPOINT_USERS } from 'src/app/shared/utils/apis';
import { AuthService } from 'src/app/shared/services/security/auth.service';

@Component({
  selector: 'app-modal-cambio-confirmacion',
  templateUrl: './modal-cambio-confirmacion.component.html',
  styleUrls: ['./modal-cambio-confirmacion.component.scss']
})
export class ModalCambioConfirmacionComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ModalCambioConfirmacionComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private loginService: LoginService,
    private alertService: ToastrService,
    private _authServ: AuthService,
  ) { }

  ngOnInit(): void {
  }
  closeModal(): void {
    this.dialogRef.close(false);
  }
  changePassword() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.CHANGE_PASSWORD;
    parametro.data = {
      actualcontrasenia: this.data.currentPass,
      nuevacontrasenia: this.data.newPass

    }
    parametro.request = 'POST';
    this.loginService.changePassword(parametro).subscribe((data: any) => {
      if (data.response.code == 200 ) {
        this.dialogRef.close(false);
        this.alertService.success(data.response.data);
        this._authServ.SignOut();
      }
    });
  }
}
