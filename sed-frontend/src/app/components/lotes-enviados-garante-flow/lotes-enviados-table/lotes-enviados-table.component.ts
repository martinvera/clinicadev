import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Garante } from 'src/app/shared/model/common/garante';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { Estado } from 'src/app/shared/model/common/status';
import { Lote } from 'src/app/shared/model/entity/lote';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { EnviadoGarante } from 'src/app/shared/services/clinica-internacional/enviado_garante.service';
import { EstatusService } from 'src/app/shared/services/clinica-internacional/estatus.service';
import { ENPOINT_ENVIADOS_GARANTE } from 'src/app/shared/utils/apis';
import { LotesEnviadosLogComponent } from '../lotes-enviados-log/lotes-enviados-log.component';
import { LotesEnviadosModalComponent } from '../lotes-enviados-modal/lotes-enviados-modal.component';


@Component({
  selector: 'app-lotes-enviados-table',
  templateUrl: './lotes-enviados-table.component.html',
  styleUrls: ['./lotes-enviados-table.component.scss']
})
export class LotesEnviadosTableComponent implements OnInit {

  page = 1;
  pageSize: any;
  pageSizeOptions: any[];
  pagination: any[];
  siguiente: boolean;
  atrasApi: string;
  siguienteApi: string;

  messaje: string = 'Debe seleccionar garante para poder realizar una búsqueda.';

  displayedColumns: string[] = ['position', 'name', 'weight', 'symbol', 'fecha Rechazo', 'garante', 'origen', 'usuario', 'estado', 'Nro sinistro', 'opciones']
  dataSource = new MatTableDataSource<Lote>();
  permission = Permission;

  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  applyFilter(event: Event) {

    const filterValue = (event.target as HTMLInputElement).value;
    if (filterValue.length >= 3) {
      this.dataSource.filter = filterValue.trim().toLowerCase();
      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    }

  }
  garantes: Garante[];

  estados: Estado[];
  form: FormGroup;
  constructor(
    private catalogoService:CatalogoService,
    public dialog: MatDialog,
    private fb: FormBuilder,
    private enviadosGaranteService: EnviadoGarante,
    private pipe: DatePipe,
    private statusService: EstatusService,
  ) {
    this.pageSize = this.catalogoService.itemInitial();
    this.pagination = [];
    this.siguiente = true;
    this.atrasApi = null;
    this.siguienteApi = null;
    this.pageSizeOptions = this.catalogoService.itemArrayNumber();

    this.estados = this.statusService.listaEstatusEnviadoGarante();
  }
  openEvent(e: MatDatepickerInputEvent<Date>) {
    const fechaHasta = (<HTMLInputElement>document.getElementById('fechaLoteHasta'))?.value ;
    const today = new Date();
    if (fechaHasta == '' || fechaHasta == undefined) {
      this.form.get('fechaLoteHasta')?.setValue(today);
    }
  }
  ngOnInit(): void {
    this.filtroForm();
    this.getListGarante();
    this.dataSource.paginator = this.paginator;
  }
  
  filtroForm() {
    this.form = this.fb.group({
      nroLote: [null],
      estado: [null],
      estadoGarante: [null],
      garanteId: [null, Validators.required],
      garanteDescripcion: [null],
      fechaLoteDesde: [null],
      fechaLoteHasta: [null],

    });
  }




  getListGarante() {

    this.catalogoService.listaGarante().subscribe(
      value => {
        this.garantes = value.response.data
      }
    )
  }


  openDialog(data) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    this.dialog.open(LotesEnviadosModalComponent, {
      data: data
    }).afterClosed().subscribe(async (data2: any) => {
      if (data?.estado) {
        this.filtro();
        this.ngAfterViewInit();
      } else {
        this.messaje = "No se encontró ningún registro.";
      }
    });
  }

  openLog(data) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    this.dialog.open(LotesEnviadosLogComponent, {
      data: data
    });
  }
  searchFilter() {
    this.cleanPagination();
    this.getApiFltroList('filtro');
  }
  filtro() {
    this.getApiFltroList('filtro');
  }

  getApiFltroList(accion?: string) {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_ENVIADOS_GARANTE.LISTA_ENVIADOS_ALGARANTE;
    parametro.request = 'POST';
    
    if (accion == 'filtro') {
      parametro.data = {
        request: {
          nroLote: this.form.value.nroLote,
          estado: 'TERMINADO',
          estadoGarante: this.form.value.estadoGarante,
          garanteId: this.form.value.garanteId,
          garanteDescripcion: this.form.value.garanteDescripcion,
          fechaLoteDesde: this.form.value.fechaLoteDesde !== "" && this.form.value.fechaLoteDesde !== null ? this.catalogoService.formaDate(this.form.value.fechaLoteDesde): null,
          fechaLoteHasta: this.form.value.fechaLoteHasta !== "" && this.form.value.fechaLoteHasta !== null ? this.catalogoService.formaDate(this.form.value.fechaLoteHasta): null,
          size: this.pageSize,
          atras: this.atrasApi,
          siguiente: this.siguienteApi
        }
      }
    } else {
      parametro.data = {
        request: {
          nroLote: "",
          estado: "TERMINADO",
          garanteId: "",
          fechaLoteDesde: "",
          fechaLoteHasta: "",
          actual: this.page,
          size: this.pageSize
        }
      }
    }
    this.enviadosGaranteService.getLote(parametro).
      subscribe(
        (value: any) => {
          if (value.response.data.length > 0) {
            this.dataSource = value.response.data;
            if (value.response.paginacion) {
              this.siguiente = false;
              let pagi = {
                siguiente: value.response.paginacion.siguiente,
                atras: value.response.paginacion.atras
              }
              this.pagination[this.page+1] = pagi;
            } else {
              this.siguiente = true;
            }
           
          } else {
            this.dataSource = value.response.data;
            this.messaje = "No se encontró ningún registro."
          }
        }
      )
  }

  public handlePage(e: any) {
    this.page = e.pageIndex + 1;
    this.pageSize = e.pageSize;
    this.dataSource = new MatTableDataSource([]);
    this.filtro();
  }
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  clearForm() {
    
    this.form.reset();
    this.dataSource = new MatTableDataSource([]);
    this.messaje = 'Debe seleccionar un garante para iniciar la búsqueda.';
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
    this.pagination = [];
    this.page = 1;
    this.atrasApi = null;
    this.siguienteApi = null;
    this.filtro();
  }
  cleanPagination() {
    this.pagination = [];
    this.page = 1;
    this.atrasApi = null;
    this.siguienteApi = null;
    
  }
}
