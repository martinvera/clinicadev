import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { RolesService } from 'src/app/shared/services/clinica-internacional/roles.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { CrearRolComponent } from '../crear-rol/crear-rol.component';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { ENDPOINT_USERS } from 'src/app/shared/utils/apis';
import { ViewRolComponent } from '../view-rol/view-rol.component';
import { Permission } from 'src/app/permission/models/permissions.enum';

export interface UserData {
  codigo: string;
  nombres: string;
  apellidoMaterno: string;
  apellidoPaterno: string;
  cargo: string;
  empresa: string;
  role: string;
  estado: number;
}

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit, AfterViewInit {
  dataUser: any = [];
  dellSuscribe: Subscription = new Subscription();

  displayedColumns: string[] = ['nombre-rol', 'estado', 'opciones'];
  dataSource: MatTableDataSource<UserData>;

  inputFilter: any;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator | any;
  @ViewChild(MatSort, { static: true }) sort: MatSort | any;
  permission = Permission;
  constructor(
    private rolesService: RolesService,
    public dialog: MatDialog,
    private toastrService: ToastrService,
  ) {
    this.dataSource = new MatTableDataSource();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }


  ngOnInit() {
    this.getListRoles();
  }

  newRole(): void {
    this.openDialog(null);
  }

  edit(row:any) {
    this.openDialog(row);
  }

  view(data:any): void {
    this.openViewRole(data);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  clearFilter(){
    this.dataSource.filter = '';
    this.inputFilter = '';
  }

  openDialog(dataItem: any): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "60%";
    this.clearFilter();
    ///this.dataSource.filter = '';
    this.dialog.open(CrearRolComponent, {
      data: dataItem
    }).afterClosed().subscribe(async (data: any) => {
      if (data) {
        if (data.response.code == 200) {
          this.toastrService.success('Acción Exitosa');
          this.getListRoles();
        }
      }
    });
  }

  getListRoles(): void {
    let params = {
      size: '10000'
    }
    const parametro: Parameter = new Parameter();
    parametro.url =  ENDPOINT_USERS.ROLLIST;
    parametro.data = params;
    parametro.request = 'GET';
    this.dellSuscribe.add(this.rolesService.getRoles(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.dataSource = new MatTableDataSource(data.response.data);
        this.ngAfterViewInit();
      }
    ));
  }

  openViewRole(item: any): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "60%";
    this.clearFilter();
    this.dialog.open(ViewRolComponent, {
      data: item
    });
  }

}

