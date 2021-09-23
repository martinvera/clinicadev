import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Garante } from 'src/app/shared/model/common/garante';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { Estado } from 'src/app/shared/model/common/status';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { EstatusService } from 'src/app/shared/services/clinica-internacional/estatus.service';
import { GestionService } from 'src/app/shared/services/clinica-internacional/gestion.service';
import { ENDPOINT_EXPED } from 'src/app/shared/utils/apis';
import { ModalErrorComponent } from '../modal-error/modal-error.component';
import { ModalReporteComponent } from '../modal-reporte/modal-reporte.component';

export interface UserData {
  facturaNro: string;
  fechaCreacion: string;
  garanteDescripcion: string;
  urlArchivoZip: string;
  estado: string;
  creadoPor: string;
}
@Component({
  selector: 'app-table-moni',
  templateUrl: './table-moni.component.html',
  styleUrls: ['./table-moni.component.scss']
})
export class TableMoniComponent implements OnInit {
  page = 1;
  pageSize: number;
  pageSizeOptions: any[];
  pagination: any[];
  siguiente: boolean;
  atrasApi: string;
  siguienteApi: string;

  dellSuscribe: Subscription = new Subscription();
  displayedColumns: string[] = ['facturaNro','tipoFactura', 'fechaCreacion', 'estado', 'garanteDescripcion', 'creadoPor', 'detalle'];
  dataSource: MatTableDataSource<UserData>;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  form: FormGroup;
  estados: Estado[];
  garantes: Garante[];
  permission = Permission;
  messaje: string = 'Debe ingresar lote y garante para iniciar la búsqueda.';
  constructor(
    private expediente: GestionService,
    public dialog: MatDialog,
    private estadoService: EstatusService,
    private catalogoService:CatalogoService,
  ) {
    this.pageSizeOptions = this.catalogoService.itemArrayNumber();

    this.pageSize = this.catalogoService.itemInitial();
    this.pagination = [];
    this.siguiente = true;
    this.atrasApi = null;
    this.siguienteApi = null;

    // Assign the data to the data source for the table to render
    this.dataSource = new MatTableDataSource();
    this.estados = this.estadoService.listaEstatusMonitoreo();
  }

  ngOnInit() {
    this.getListGarante();
    this.crearForm();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  crearForm() {
    this.form = new FormGroup({
      lote: new FormControl('', Validators.required),
      garanteId: new FormControl('', Validators.required),
      garante: new FormControl('', Validators.required),
      nroFactura: new FormControl(''),
      estado: new FormControl(null),
      fechaDesde: new FormControl(''),
      fechaHasta: new FormControl('')
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  getExpedientes(data: any) {
    var cuerpo: any;
    
    if (data == undefined) {
      cuerpo = {
        header: {
          transactionId: "",
          applicationId: "",
          userId: ""
        },
        request: {
          lote: '',
          garanteId: '',
          garante: '',
          fechaDesde: "",
          fechaHasta: "",
          facturaNro: "",
          estatus: "",
          actual: this.page,
          size: this.pageSize,
          atras: this.atrasApi,
          siguiente: this.siguienteApi
        }
      };
    } else {
      cuerpo = {
        request: {
          lote: parseInt(data.lote),
          garanteId: data.garanteId,
          garante: data.garante,
          fechaDesde: this.form.value.fechaDesde !== "" && this.form.value.fechaDesde !== null ? this.catalogoService.formaDate(data.fechaDesde): null,
          fechaHasta: this.form.value.fechaHasta !== "" && this.form.value.fechaHasta !== null ? this.catalogoService.formaDate(data.fechaHasta): null,
          facturaNro: data.nroFactura,
          estado: data.estado,
          actual: this.page,
          size: this.pageSize,
          atras: this.atrasApi,
          siguiente: this.siguienteApi
        }
      };
    }
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_EXPED.FIND_EXPED
    parametro.data = cuerpo;
    parametro.request = 'POST';
    this.dellSuscribe.add(this.expediente.getExpedienteList(parametro).pipe(
    ).subscribe(
      (data: any) => {
        if (data.response.data.length > 0) {
          this.dataSource = data.response.data;
          if (data.response.paginacion) {
            this.siguiente = false;
            let pagi = {
              siguiente: data.response.paginacion.siguiente,
              atras: data.response.paginacion.atras
            }
            this.pagination[this.page+1] = pagi;
          } else {
            this.siguiente = true;
          }
        } else {
          this.dataSource = new MatTableDataSource([]);
          this.messaje = "No se encontro ningun registro"
        }
      }
    ));

  }

  openEvent(e: MatDatepickerInputEvent<Date>) {
    const fechaHasta = (<HTMLInputElement>document.getElementById('fechaHasta')).value;
    const today = new Date();
    if (fechaHasta == '') {
      this.form.get('fechaHasta')?.setValue(today);
    }
  }

  validAlphaNumber(e: any) {
    if (!e.key.match(/[a-zA-Z0-9-]/)) {
      e.preventDefault();
    }
  }

  openModal(row: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    if (row.msjError) {
      this.dialog.open(ModalErrorComponent, {
        data: row.msjError
      });
    }
  }
  searchFilter() {
    this.dellpage();
    this.getExpedientes(this.form.value);
  }
  filtro() {
    this.getExpedientes(this.form.value);
  }

  clearForm() {
    this.form.reset();
    this.dataSource = new MatTableDataSource([]);
  }

  openModalReporte() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    this.dialog.open(ModalReporteComponent);
  }
  setGarante(event) {
    let tempGarante: any = this.garantes;
    let data = tempGarante.find(value => value.codigo == event.value)
    this.form.controls.garante.setValue(data.descripcion);
  }
  //garante
  getListGarante() {

    this.catalogoService.listaGarante().subscribe(
      value => {
        this.garantes = value.response.data
      }
    )
  }
  public handlePage(e: any) {
    this.page = e.pageIndex + 1;
    this.pageSize = e.pageSize;
    this.dataSource = new MatTableDataSource([]);
    this.filtro();
  }


  goBack() {
    this.page = this.page - 1;
    this.changePage();
  }

  goForward() {
    this.page = this.page + 1;
    this.changePage();
  }
  changePage() {
    this.atrasApi = this.pagination[this.page]?.atras == undefined ? null : this.pagination[this.page].atras;
    this.siguienteApi = this.page == 1? null : this.pagination[this.page].siguiente;
    this.filtro();
  }
  setPage(event: any) {
    this.pageSize = event.target.value;
    this.filtro();
    this.pagination = [];
    this.page = 1;
  }
  dellpage() {
    this.pagination = [];
    this.page = 1;
    this.atrasApi = null;
    this.siguienteApi = null;
  }
}
