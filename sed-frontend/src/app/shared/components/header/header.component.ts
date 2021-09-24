
import { Component, OnInit } from '@angular/core';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { NavService } from '../../services/nav.service';
import { AuthService } from '../../services/security/auth.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ReprocesarColaPageComponent } from 'src/app/components/reprocesar-cola-flow/reprocesar-cola-page/reprocesar-cola-page.component';

var body = document.getElementsByTagName("body")[0];

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  userName: any;
  permission = Permission;
  constructor(public navServices: NavService,
    private _authService: AuthService,
    public dialog: MatDialog,
    ) 
    {}

  ngOnInit() {
    this.userName = this._authService.getUser();
  }

  // openDialog() {
  //   const dialogConfig = new MatDialogConfig();
  //   dialogConfig.disableClose = true;
  //   dialogConfig.autoFocus = true;
  //   dialogConfig.width = "80%";
  //   this.dialog.open(ReprocesarColaPageComponent, {
  //     //data: data
  //   }).afterClosed().subscribe(async (data1: any) => {
  //     if (data1.response.data) {
  //       //this.toastrService.success(data1.response.data);
  //       //this.filtroLote();
  //     }
  //   }); }

  SignOut() {
    this._authService.SignOut();
  }
 
}
