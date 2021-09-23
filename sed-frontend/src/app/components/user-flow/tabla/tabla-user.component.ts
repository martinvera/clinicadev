import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { UsersService } from 'src/app/shared/services/clinica-internacional/users.service';
import { ENDPOINT_USERS } from 'src/app/shared/utils/apis';
import { ModalCreateComponent } from '../modal-create/modal-create.component';
import { ModalViewComponent } from '../modal-view/modal-view.component';
export interface UserData {
  codigo: string;
  fullName: string;
  cargo: string;
  empresa: string;
  role: string;
  estado: number;
}
@Component({
  selector: 'app-tabla-user',
  templateUrl: './tabla-user.component.html',
  styleUrls: ['./tabla-user.component.scss']
})
export class TablaUserComponent implements OnInit {
  dataUser: any = [];
  dellSuscribe: Subscription = new Subscription();
  userItem: any;
  inputFilter: any;

  displayedColumns: string[] = ['fullName', 'cargo', 'role', 'estado', 'opciones'];
  dataSource: MatTableDataSource<UserData>;


  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  permission = Permission;
  constructor(
    private usersService: UsersService,
    private toastrService: ToastrService,
    public dialog: MatDialog
  ) {
    this.dataSource = new MatTableDataSource();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }


  ngOnInit() {
    this.getListUser();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  clearFilter() {
    this.dataSource.filter = '';
    this.inputFilter = '';
  }

  openDialog(dataItem: any): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "60%";
    this.clearFilter();
    this.dialog.open(ModalCreateComponent, {
      data: dataItem
    }).afterClosed().subscribe(async (data: any) => {
      if (data) {
        if (data.error) {
          this.toastrService.error(':: Verificar los datos Ingresados');
        }
        if (data.response.code == 200) {
          this.toastrService.success(':: Acción Exitosa');
          this.getListUser();
        }
      }
    });
  }

  openViewUser(item: any): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "60%";
    this.clearFilter();
    this.dialog.open(ModalViewComponent, {
      data: item
    });
  }

  newUser(): void {
    this.openDialog(null);
  }

  edit(row: any): void {
    this.openDialog(row);
  }

  getListUser() {
    let params = {
      size: '10000'
    };
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.LIST + '?size=1000';
    parametro.data = params;
    parametro.request = 'GET';
    this.dellSuscribe.add(this.usersService.getUsers(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.dataSource = new MatTableDataSource(data.response.data);
        this.ngAfterViewInit();
      }
    ));
  }

  view(data: any): void {
    this.openViewUser(data);
  }
}
