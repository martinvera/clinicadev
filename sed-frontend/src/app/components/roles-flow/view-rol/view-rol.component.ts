import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { RolesService } from 'src/app/shared/services/clinica-internacional/roles.service';
import { ENDPOINT_USERS } from 'src/app/shared/utils/apis';

@Component({
  selector: 'app-view-rol',
  templateUrl: './view-rol.component.html',
  styleUrls: ['./view-rol.component.scss']
})
export class ViewRolComponent implements OnInit {
  roleItem: any;
  dellSuscribe: Subscription = new Subscription();
  constructor(
    public dialogRef: MatDialogRef<ViewRolComponent>,
    @Inject(MAT_DIALOG_DATA) public dataRole: any,
    private rolService: RolesService,
  ) { }

  ngOnInit() {
    this.getListUser();
  }

  closeModal(): void {
    this.dialogRef.close(false);
  }
  getListUser() {
    let params = {
      size: '10000'
    };
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.GET_ROL.replace(':idRol',this.dataRole.codigo);
    parametro.data = params;
    parametro.request = 'GET';
    this.dellSuscribe.add(this.rolService.getRolId(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.roleItem = data.response.data;
      }
    ));

  }

}
