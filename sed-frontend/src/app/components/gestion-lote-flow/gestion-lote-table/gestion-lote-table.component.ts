import { SelectionModel } from '@angular/cdk/collections';
import { DatePipe } from '@angular/common';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DateAdapter } from '@angular/material/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ToastrService } from 'ngx-toastr';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { Garante } from 'src/app/shared/model/common/garante';
import { Lote } from 'src/app/shared/model/entity/lote';
import { LoteService } from 'src/app/shared/services/clinica-internacional/lote.service';
import { ENPOINT_LOTE } from 'src/app/shared/utils/apis';
import { ModalGestionLoteDeleteComponent } from '../modal-gestion-lote-delete/modal-gestion-lote-delete.component';
import { Estado } from 'src/app/shared/model/common/status';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { MatSort } from '@angular/material/sort';
import { ModalReprocesoComponent } from '../modal-reproceso/modal-reproceso.component';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
export interface PeriodicElement {
  name: string;
  nroLote: number;
  weight: number;
  estado: string;
  symbol: string;
}

export interface PassData {
  nroLote: number;
  garanteId: number;
}

@Component({
  selector: 'app-gestion-lote-table',
  templateUrl: './gestion-lote-table.component.html',
  styleUrls: ['./gestion-lote-table.component.scss'],
})
export class GestionLoteTableComponent implements OnInit, AfterViewInit {

  page = 1;
  pageSize: any;
  pageSizeOptions: any[];
  pagination: any[];
  siguiente: boolean;
  atrasApi: string;
  siguienteApi: string;

  displayedColumns: string[] = ['nroLote', 'fechaLote', 'garanteDescripcion', 'origen', 'estadoFactura', 'countFacturas', 'options'];
  dataSource = new MatTableDataSource<Lote>();
  selection = new SelectionModel<Lote>(true, []);
  public colSize = 7;
  public isMobile: boolean = false;
  messaje: string = 'Debe ingresar garante para iniciar la búsqueda.';

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
  estados: Estado[];
  garantes: Garante[];
  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }
  form: FormGroup;
  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: Lote): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.nroLote + 1}`;
  }
  permission = Permission;
  pageEvent: PageEvent;
  constructor(
    public dialog: MatDialog,
    private loteService: LoteService,
    private toastrService: ToastrService,
    private fb: FormBuilder,
    private dateAdapter: DateAdapter<any>,
    private catalogoService:CatalogoService,
    private pipe: DatePipe,
  ) {
    this.pageSize = this.catalogoService.itemInitial();
    this.pagination = [];
    this.siguiente = true;
    this.atrasApi = null;
    this.siguienteApi = null;

    this.pageSizeOptions = this.catalogoService.itemArrayNumber();
    this.filtroForm();
    this.dateAdapter.setLocale("es");
    this.garantes = [];
    this.estados = this.catalogoService.listaEstatus();


  }

  ngOnInit() {
    this.dataSource.paginator = this.paginator;
    this.getListGarante();
  }

  filtroForm() {
    this.form = this.fb.group({
      nroLote: [null],
      estado: [null],
      garanteId: [null, Validators.required],
      garanteDescripcion: [null, Validators.required],
      fechaLoteDesde: [null],
      fechaLoteHasta: [null],
    });
  }

  clearForm() {
    this.form.reset();
    this.filtroForm();
    this.dataSource = new MatTableDataSource([]);
    this.ngAfterViewInit();
    this.messaje = 'Debe seleccionar un garante para iniciar la búsqueda.';

  }
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  openDialog(data: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80%";
    this.dialog.open(ModalGestionLoteDeleteComponent, {
      data: data
    }).afterClosed().subscribe(async (data1: any) => {
      if (data1.response.data) {
        this.toastrService.success(data1.response.data);
        this.filtroLote();
      }
    });
  }

  openReprocess(data: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80%";
    this.dialog.open(ModalReprocesoComponent, {
      data: data
    }).afterClosed().subscribe(async (data2: any) => {
      if (data2.response.data) {
        this.toastrService.success(data2.response.data);
        this.filtroLote();
      }
    });
  }

  enviarData: PassData;
  goToExpediente(data: any) {
    this.catalogoService.nroLote = data.rowKey;
    this.catalogoService.garateId = this.form.get('garanteId').value;

  }

  getListLote() {
    this.getApiFltroList();
  }

  searchFilter() {

    this.cleanPagination();
    this.getApiFltroList('filtro');
  }
  filtroLote() {
    this.getApiFltroList('filtro');
  }

  getApiFltroList(accion?: string) {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_LOTE.LIST_LOTE;
    parametro.request = 'POST';
    
    if (accion == 'filtro') {
      parametro.data = {
        request: {
          nroLote: this.form.value.nroLote,
          estado: this.form.value.estado,
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
          estado: "",
          garanteId: "",
          fechaLoteDesde: "",
          fechaLoteHasta: "",
          actual: this.page,
          size: this.pageSize
        }
      }
    }

    this.loteService.getLote(parametro).
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
              this.pagination[this.page + 1] = pagi;
            } else {
              this.siguiente = true;
            }
          } else {
            this.dataSource = value.response.data;
            this.cleanPagination();
            this.messaje = "No se encontró ningún registro."
          }
        }
      )
  }

  getListGarante() {
    this.catalogoService.listaGarante().subscribe(
      value => {
        this.garantes = value.response.data
      }
    )
  }

  openEvent(e: MatDatepickerInputEvent<Date>) {
    const fechaHasta = (<HTMLInputElement>document.getElementById('fechaLoteHasta')).value;
    const today = new Date();
    if (fechaHasta == '') {
      this.form.get('fechaLoteHasta')?.setValue(today); //error al hacer ser de las fechas
    }
  }

  // opcionSeleccionado: any;
  setGarante(event) {
    let tempGarante: any = this.garantes;
    let data = tempGarante.find(value => value.codigo == event.value)
    this.form.controls.garanteDescripcion.setValue(data.descripcion);
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
    this.siguienteApi = this.page == 1 ? null : this.pagination[this.page].siguiente;
    this.filtroLote();
  }
  setPage(event: any) {
    this.pageSize = event.target.value;
    this.pagination = [];
    this.page = 1;
    this.atrasApi = null;
    this.siguienteApi = null;
    this.filtroLote();
  }
  cleanPagination() {

    this.pagination = [];
    this.page = 1;
    this.atrasApi = null;
    this.siguienteApi = null;


  }

}
