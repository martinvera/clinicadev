import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ToastrService } from 'ngx-toastr';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Garante } from 'src/app/shared/model/common/garante';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { DocumentsService } from 'src/app/shared/services/clinica-internacional/documents.service';
import { GestionService } from 'src/app/shared/services/clinica-internacional/gestion.service';
import { ENDPOINT_DOCUMENTS } from 'src/app/shared/utils/apis';
import { ModalEditComponent } from '../modal-edit/modal-edit.component';
import { ModalReportesinloteComponent } from '../modal-reportesinlote/modal-reportesinlote.component';
import { ModalVisualizarComponent } from '../modal-visualizar/modal-visualizar.component';

@Component({
  selector: 'app-table-docs',
  styleUrls: ['./table-docs.component.scss'],
  templateUrl: './table-docs.component.html',
})


export class TableDocsComponent implements OnInit {

  page: any = 1;
  pageSize: number;
  pageSizeOptions: any[];
  pagination: any[];
  siguiente: boolean;
  atrasApi: string;
  siguienteApi: string;
  atrasboolean: boolean = false;

  displayedColumns: string[] =
    [
      'nroEncuentro',
      'pacienteNombre',
      'pacienteApellidoPaterno',
      'pacienteApellidoMaterno',
      'facturaNro',
      'fechaAtencion',
      'sede',
      'facturaImporte',
      'garanteDescripcion',
      'nroLote',
      'nroRemesa',
      'pacienteTipoDocIdentDesc',
      'pacienteNroDocIdent',
      'origen',
      'usuario',
      'opciones'
    ];
  range: FormGroup;
  dataSource = new MatTableDataSource<DocData>();
  step = 0;
  xpandStatus = true;

  messaje: string = 'Debe ingresar un lote inicial y lote final para poder realizar una búsqueda.';
  fechaHasta: any;
  garantes: Garante[];
  permission = Permission;
  constructor(
    private gestionservice: GestionService,
    public dialog: MatDialog,
    private catalogoService:CatalogoService,
    private alertService: ToastrService,
    private pipe: DatePipe,
    private documentoService: DocumentsService,
  ) {

    this.pageSize = this.catalogoService.itemInitial();
    this.pagination = [];
    this.siguiente = true;
    this.atrasApi = null;
    this.siguienteApi = null;


    this.pageSizeOptions = this.catalogoService.itemArrayNumber();
    this.dataSource = new MatTableDataSource();
  }
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  form: FormGroup;

  applyFilter(event: Event) {

    const filterValue = (event.target as HTMLInputElement).value;
    if (filterValue.length >= 3) {
      this.dataSource.filter = filterValue.trim().toLowerCase();
      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    }
  }

  setStep(index: number) {
    this.step = index;
  }


  crearForm() {
    this.form = new FormGroup({
      nroLoteDesde: new FormControl('', Validators.required),
      nroLoteHasta: new FormControl('', Validators.required),
      fechaAdmisionDesde: new FormControl(''),
      fechaAdmisionHasta: new FormControl(''),
      nroFactura: new FormControl(''),
      nroEncuentro: new FormControl(''),
      remesa: new FormControl(''),
      garante: new FormControl(''),
      importeFactura: new FormControl(''),
      importeOperacion: new FormControl('')
    })
  }
  openViewDocument(item: any): void {


    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_DOCUMENTS.CONSULTA_DOCUMENTO;

    parametro.data = {
      "request": {
        nroEncuentro: item.nroEncuentro,
        nroLote: item.nroLote,
        facturaNro: item.facturaNro
      }
    };
    parametro.request = 'POST';
    this.documentoService.getDocument(parametro).subscribe(
      (value: any) => {
        if (value.response.code == 200 && value.response.data) {
          const dialogConfig = new MatDialogConfig();
          dialogConfig.disableClose = true;
          dialogConfig.autoFocus = true;
          dialogConfig.width = "60%";
          this.dialog.open(ModalVisualizarComponent, {
            data: value.response.data
          });
        }
      });
  }

  openViewReportesinlote(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "60%";
    this.dialog.open(ModalReportesinloteComponent).afterClosed().subscribe(async (data: any) => {
      if (data.response.code == 200) {
        this.alertService.success(data.response.data);
      }
    });
  }

  openEditDocument(item: any): void {

    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_DOCUMENTS.CONSULTA_DOCUMENTO;
    parametro.data = {
      "request": {
        nroEncuentro: item.nroEncuentro,
        nroLote: item.nroLote,
        facturaNro: item.facturaNro
      }
    };
    parametro.request = 'POST';
    this.documentoService.getDocument(parametro).subscribe(
      (value: any) => {
        if (value.response.code == 200 && value.response.data) {
          const dialogConfig = new MatDialogConfig();
          dialogConfig.disableClose = true;
          dialogConfig.autoFocus = true;
          dialogConfig.width = "60%";
          this.dialog.open(ModalEditComponent, {
            data: value.response.data
          }).afterClosed().subscribe(async (data: any) => {
            if (data) {
              if (data.response.code == 200) {

                this.findDocs(this.form.value);
              }
            }
          });
        }
      });


  }

  createForm() {
    this.range = new FormGroup({
      start: new FormControl(''),
      end: new FormControl(''),
    });
  }


  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }


  ngOnInit() {

    this.createForm();
    this.dataSource.paginator = this.paginator;

    this.crearForm();
    this.getListGarante();
  }

  searchFilter() {
    this.dellpage();
    this.findDocs(this.form.value);
  }
  filter() {
    this.findDocs(this.form.value);
  }

  validAlphaNumber(e: any) {
    if (!e.key.match(/[a-zA-Z0-9-]/)) {
      e.preventDefault();
    }
  }

  findDocs(data: any) {

    var cuerpo: any;
    if (data == undefined) {
      cuerpo = {
        header: {
          transactionId: "",
          applicationId: "",
          userId: ""
        },
        request: {
          actual: this.page,
          size: this.pageSize
        }
      }

    } else {
      let fechaAdmision;
      let fechaAdminsionh;
      if (data.fechaAdmisionDesde !== "" && data.fechaAdmisionDesde !== null) {
        const date = new Date(data.fechaAdmisionDesde);
        date.setDate(date.getDate());
        fechaAdmision = this.pipe.transform(date, 'yyyy-MM-dd');
      } else {
        fechaAdmision = null;
      }
      if (data.fechaAdmisionHasta !== "" && data.fechaAdmisionHasta !== null) {
        const date2 = new Date(data.fechaAdmisionHasta);
        date2.setDate(date2.getDate());
        fechaAdminsionh = this.pipe.transform(date2, 'yyyy-MM-dd');
      } else {
        fechaAdminsionh = null;
      }

      cuerpo = {
        header: {
          transactionId: "",
          applicationId: "",
          userId: ""
        },
        request: {
          nroLoteDesde: data.nroLoteDesde,
          nroLoteHasta: data.nroLoteHasta,
          fechaAtencionDesde: fechaAdmision,
          fechaAtencionHasta: fechaAdminsionh,
          facturaNro: data.nroFactura,
          remesa: data.remesa,
          garanteId: data.garante,
          importeFactura: data.importeFactura,
          importeOperacion: data.importeOperacion,
          nroEncuentro: data.nroEncuentro,
          origenServicio: data.origenServicio,
          actual: this.page,
          size: this.pageSize,
          atras: '',
          siguiente: this.page == 1 ? '' : this.siguienteApi
        }
      }
    }

    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_DOCUMENTS.DOCUMENTLIST;
    parametro.data = cuerpo;
    parametro.request = 'POST';
    this.gestionservice.findDoc(parametro).subscribe((data: any) => {
      if (data.response.data.length > 0) {
        this.dataSource = data.response.data;
        if (data.response.paginacion) {
          if (this.atrasboolean == false) {
            this.pagination[this.page + 1] = data.response.paginacion;
          }
          if (data.response.paginacion.siguiente != null) {
            this.siguiente = false;
          } else {
            this.siguiente = true;
          }
        } else {
          this.siguiente = true;
        }
      } else {
        this.dataSource = data.response.data;
        this.messaje = "No se encontro ningun registro"
      }
      this.atrasboolean = false;
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }


  view(data: any) {
    this.openViewDocument(data);
  }

  newReporSLote() {
    this.openViewReportesinlote();
  }

  getListGarante() {

    this.catalogoService.listaGarante().subscribe(
      value => {
        this.garantes = value.response.data;
      }
    )
  }



  edit(data: any) {
    this.openEditDocument(data);
  }

  clearForm() {
    this.form.controls.nroLoteHasta.clearValidators();
    this.form.controls.nroLoteDesde.clearValidators();
    this.form.updateValueAndValidity();
    this.dataSource = new MatTableDataSource([]);
    this.ngAfterViewInit();
    this.form.controls.importeFactura.clearValidators();
    this.form.controls.importeFactura.updateValueAndValidity();
    this.form.controls.importeFactura.setValue(null);
    this.form.reset();
    this.messaje = 'Debe ingresar un lote de inicial, lote final y el garante para poder realizar una busqueda.';
  }

  openEvent(e: MatDatepickerInputEvent<Date>) {
    const fechaHasta = (<HTMLInputElement>document.getElementById('fechaHasta')).value;
    const today = new Date();
    if (fechaHasta == '') {
      this.form.get('fechaAdmisionHasta')?.setValue(today);
    }
  }

  onSelectImporte(e: any) {
    const importeFactura = (<HTMLInputElement>document.getElementById('importeFactura')).value;
    if (importeFactura == '') {

      this.form.controls.importeFactura.setValue(null);
      this.form.controls.importeFactura.setValidators([Validators.required]);
      this.form.controls.importeFactura.updateValueAndValidity();
    }
  }

  validLoteDesde(e: any) {
    this.form.controls.nroLoteHasta.setValidators([
      Validators.required
    ])
    this.form.controls.nroLoteHasta.updateValueAndValidity();
  }

  validLoteHasta(e: any) {
    this.form.controls.nroLoteDesde.setValidators([
      Validators.required,
    ]);
    this.form.controls.nroLoteDesde.updateValueAndValidity();
  }
  public handlePage(e: any) {
    this.page = e.pageIndex + 1;
    this.pageSize = e.pageSize;
    this.dataSource = new MatTableDataSource([]);
    this.filter();
  }
  goBack() {
    this.atrasboolean = true
    this.page = this.page - 1;
    this.siguienteApi = this.page == 1 ? '' : this.pagination[this.page].siguiente;
    this.filter();
  }

  goForward() {
    this.page = this.page + 1;
    this.siguienteApi = this.pagination[this.page].siguiente;
    this.filter();
  }
  setPage(event: any) {
    this.pageSize = event.target.value;
    this.pagination = [];
    this.page = 1;
    this.atrasApi = null;
    this.siguienteApi = null;
    this.filter();
  }

  dellpage() {
    this.pageSize = this.catalogoService.itemInitial();
    this.pagination = [];
    this.page = 1;
    this.atrasApi = null;
    this.siguienteApi = null;
  }

}

export interface DocData {
  nroEncuentro: string;
  pacienteNombre: string;
  pacienteApellidoPaterno: string;
  pacienteApellidoMaterno: string;
  facturaNro: string;
  fechaAtencion: string;
  sede: string;
  facturaImporte: string;
  garanteDescripcion: string;
  nroLote: string;
  nroRemesa: string;
  pacienteTipoDocIdentDesc: string;
  pacienteNroDocIdent: string;
  origenDescripcion: string;
  userId: string;
}




