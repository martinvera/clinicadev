import { SelectionModel } from '@angular/cdk/collections';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ToastrService } from 'ngx-toastr';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Garante } from 'src/app/shared/model/common/garante';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { Estado } from 'src/app/shared/model/common/status';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { EstatusService } from 'src/app/shared/services/clinica-internacional/estatus.service';
import { GestionService } from 'src/app/shared/services/clinica-internacional/gestion.service';
import { ENDPOINT_EXPED } from 'src/app/shared/utils/apis';
import { DocumentosModalComponent } from '../documentos-modal/documentos-modal.component';
import { ModalValidateComponent } from '../modal-validate/modal-validate.component';


export interface ExpedienteData {
  id: string,
  origen: string,
  nroLote: string,
  mecanismoFacturacionDesc: string,
  mecanismoFacturacionId: string,
  nroEncuentro: any,
  nrosEncuentro: string,
  pacienteTipoDocIdentDesc: string,
  pacienteTipoDocIdentId: string,
  pacienteNroDocIdent: string,
  pacienteNombre: string,
  pacienteApellidoPaterno: string,
  pacienteApellidoMaterno: string,
  ordenAtencionDocumento: string,
  garanteDescripcion: string,
  garanteId: string,
  nroFactura: string,
  importeFactura: string,
  archivoAnexoDet: string,
  archivoFactura: string,
  nroHistoriaClinica: string,
  facturaNro: string,
  facturaImporte: string,
  estado: boolean,
  estadoFactura: string,
  exonerado?: boolean,
  origenServicio?: string,
  fechaAtencion?: string,
}

export interface TestFacturas {
  facturaNro: string,
  importeFactura: string,
  origen: string,
  mecanismoFacturacion: string,
  garanteDescripcion: string,
  nroRemesa: string,
  estadoFactura: string,
  PFactura: string,
  Anexo: string,
  exonerado?: boolean
}

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
  selector: 'app-table-expediente',
  templateUrl: './table-expediente.component.html',
  styleUrls: ['./table-expediente.component.scss']
})
export class TableExpedienteComponent implements OnInit {
  page: any = 1;
  pageSize: number;
  pageSizeOptions: any[];
  pagination: any[];
  siguiente: boolean;
  atrasApi: string;
  siguienteApi: string;
  atrasboolean: boolean = false;

  selection = new SelectionModel<ExpedienteData>(true, []);
  selection2 = new SelectionModel<ExpedienteData>(true, []);
  @Input() data: any;
  dataUser: any = [];
  respuesta: any;
  displayedColumns: string[] = ['select', 'factura', 'tipoFactura', 'fAsociada' , 'importe', 'origen', 'origenServicio', 'mecanismoFacturacion', 'garante', 'modoFacturacion', 'nroRemesa', 'estado', 'ejecutar', 'PFactura', 'Anexo', 'Documentos'];
  displayedColumns2: string[] = ['Encuentro'];
  dataSource: MatTableDataSource<ExpedienteData>;
  dataSourceModal: MatTableDataSource<ExpedienteData>;
  listaExpedientes: any;
  numLoteGeneral: any;
  garantes: Garante[];
  estados: Estado[];

  checkSelect: any = [];

  executeSelect: any = [];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  form: FormGroup;
  datosExpedientes: any;
  permission = Permission;
  messaje: string;
  constructor(
    public dialog: MatDialog,
    public gestionService: GestionService,
    private toastrService: ToastrService,
    private statusService: EstatusService,
    private catalogoService:CatalogoService,
  ) {
    this.pageSize = this.catalogoService.itemInitial();
    this.pagination = [];
    this.siguiente = true;
    this.atrasApi = null;
    this.siguienteApi = null;


    this.pageSizeOptions = this.catalogoService.itemArrayNumber();
    this.dataSource = new MatTableDataSource();
    this.dataSourceModal = new MatTableDataSource();
    this.estados = this.statusService.listaEstatusGenerarExpediente();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  validAlphaNumber(e: any) {
    if (!e.key.match(/[a-zA-Z0-9-]/)) {
      e.preventDefault();
    }
  }

  ngOnInit() {
    this.getListGarante();
    this.crearForm();
    this.getLote();
  }



  getLote() {
    if (this.catalogoService.nroLote > 0) {
      setTimeout(() => {
        this.numLoteGeneral = this.catalogoService.transportLote();
        this.form.get('nroLote').patchValue(this.catalogoService.transportLote())
        this.form.get('garanteId')?.setValue(this.catalogoService.transporteGarante());
        this.setGaranteTransporte(this.catalogoService.transporteGarante());
        this.filtro();
      }, 2000);
    } else {
      this.numLoteGeneral = null;
    }
  }

  searchFilter() {
    this.dellpage();
    this.getExpedientes(this.form.value);
  }
  filtro() {
    this.getExpedientes(this.form.value);
  }




  crearForm() {
    this.form = new FormGroup({
      facturaNro: new FormControl(''),
      nroRemesa: new FormControl(''),
      garante: new FormControl('', Validators.required),
      garanteId: new FormControl('', Validators.required),
      estatus: new FormControl(''),
      nroLote: new FormControl(''),

    })
  }


  getExpedientes(data: any) {
    var requestJSON: any;
    var cuerpo: any;


    if (data == undefined) {
      cuerpo = {
        nroLote: this.numLoteGeneral,
        nroRemesa: null,
        estatus: null,
        garanteId: null,
        facturaNro: null,
        actual: this.page,
        size: this.pageSize
      }

    } else {
      if (data.nroRemesa == '') {
        data.nroRemesa = null;
      }

      if (data.garante == '') {
        data.garante = null;
      }

      if (data.estatus == '') {
        data.estatus = null;
      }

      if (data.facturaNro == '') {
        data.facturaNro = null;
      }

      cuerpo = {
        nroLote: this.numLoteGeneral,
        nroRemesa: data.nroRemesa,
        garanteDescripcion: data.garante,
        garanteId: data.garanteId,
        estatus: data.estatus,
        facturaNro: data.facturaNro,
        actual: this.page,
        size: this.pageSize,
        atras: '',
        siguiente: this.page == 1 ? '' : this.siguienteApi
      }
    }

    requestJSON = {
      header: {
        transactionId: "",
        applicationId: "",
        userId: ""
      },
      request: cuerpo
    }

    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_EXPED.LIST_RECORD
    parametro.data = requestJSON;
    parametro.request = 'POST';
    this.gestionService.getExpedt(parametro).subscribe(data => {
      if (data.response.data.length > 0) {
        this.dataSource = new MatTableDataSource(this.mapExonerado(data.response.data));
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
        this.dataSource = new MatTableDataSource(data.response.data);
        this.messaje = "No se encontró ningún registro con el número de lote seleccionado."
      }
      this.atrasboolean = false;
    });
  }

  mapExonerado(data: any) {
    data.map((value: any) => {
      value.exonerado = 0;
    });

    return data;
  }


  docModal(item: any) {
    this.openDialog(item);
  }


  openDialog(data: any): void {
    let cuerpo = {
      request: {
        nroLote: this.numLoteGeneral,
        facturaNro: data.facturaNro,
        garanteid: data.garanteId,
        mecanismoid: data.mecanismoFacturacionId,
        modofacturacionid: data.modoFacturacion,
      },

    }
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_EXPED.LIST_DOCUMENT;
    parametro.data = cuerpo;
    parametro.request = 'POST';
    this.gestionService.listDocument(parametro).subscribe(async (value: any) => {
      if (value.response.data.length != 0) {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.disableClose = true;
        this.dialog.open(DocumentosModalComponent, {
          data: {
            data,
            documento: value.response.data
          }
        });
      } else {
        this.toastrService.warning(':: No tiene documentos asociados');
      }
    });


  }


  openAlert(e: any, i: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "60%";
    this.dialog.open(ModalValidateComponent)
      .afterClosed()
      .subscribe(async (data: any) => {
        if (data == false) {
          this.checkSelect[i] = false;
        }
      });
  }



  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected() == true) {
      this.checkSelect = [];
      this.executeSelect = [];
      this.selection.clear();
      this.dataSource.data.forEach((row: any) => {
        this.selection.deselect(row);
      });
    } else {
      if (this.isAllSelected() == false) {
        this.dataSource.data.forEach((row: any) => {
          if (row.estadoFactura == 'COMPLETO') {
            this.selection.select(row);
          }
        });
      }
    }
  }


  validStatus(e: any, data: any, index: any) {
    if (e.checked == true) {
      if (data.estadoFactura !== 'COMPLETO') {
        this.openAlert(e, index);
        this.selection.deselect(data);
      } else {
        this.checkSelect[index] = true;
        this.selection.toggle(data);
      }
    } else {
      if (this.executeSelect[index] == true) {
        this.executeSelect[index] = false;
      }
      this.checkSelect[index] = false
      this.selection.deselect(data);
    }
  }

  changeExecute(e: any, data: any, index: any) {
    let specilBoolean: any = 0;
    if (e.checked !== true) {
      specilBoolean = 0;
      this.executeSelect[index] = false;
      this.checkSelect[index] = false;
    } else {
      specilBoolean = 1;
      this.executeSelect[index] = true;
      this.checkSelect[index] = true;
    }

    this.selection.toggle(data);
    this.dataSource.data.forEach((value: any) => {
      if (value.facturaNro == data.facturaNro) {
        value.exonerado = specilBoolean;
      }
    });
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: ExpedienteData): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.id + 1}`;
  }

  checkboxLabelEjecutar(row?: ExpedienteData): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection2.isSelected(row) ? 'deselect' : 'select'} row ${row.id + 1}`;
  }



  generarExpediente() {
    var cuerpo: any;
    var requestJSON: any;

    var datos: any = [];
    this.selection.selected.forEach(function (elemento: any) {
      datos.push({
        facturaNro: elemento.facturaNro,
        facturaImporte: elemento?.facturaImporte,
        ejecutar: elemento.exonerado,
        nroLote: elemento.nroLote,
        garanteId: elemento?.garanteId,
        garanteDescripcion: elemento?.garanteDescripcion,
        origen: elemento?.origen,
        mecanismoFacturacionId: elemento?.mecanismoFacturacionId,
        mecanismoFacturacionDesc: elemento?.mecanismoFacturacionDesc,
        modoFacturacionId: elemento?.modoFacturacionId,
        modoFacturacion: elemento?.modoFacturacion,
        beneficioDescripcion: elemento?.beneficioDescripcion,
        pacienteTipoDocIdentDesc: elemento?.pacienteTipoDocIdentDesc,
        pacienteNroDocIdent: elemento?.pacienteNroDocIdent,
        pacienteApellidoMaterno: elemento?.pacienteApellidoMaterno,
        pacienteApellidoPaterno: elemento?.pacienteApellidoPaterno,
        pacienteNombre: elemento?.pacienteNombre,
        nroEncuentro: elemento?.nroEncuentro[0],
        fechaAtencion: elemento?.fechaAtencion,
      });
    });
    requestJSON = {
      header: {
        transactionId: "",
        applicationId: "",
        userId: ""
      },
      request: datos
    }


    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_EXPED.GEN_EXPE;
    parametro.data = requestJSON;
    parametro.request = 'POST';
    if ((datos).length > 0) {
      this.gestionService.generarExpediente(parametro).pipe().subscribe((data: any) => {
        this.getExpedientes(this.form);
        this.ngAfterViewInit();
        if (data.response.code == 200) {
          this.selection.clear();
          this.selection2.clear();
          this.toastrService.success(':: Generación enviada');
          this.checkSelect = [];
          this.executeSelect = [];
          this.filtro();
        } else {
          this.selection.clear();
          this.selection2.clear();
          this.toastrService.error(':: Seleccione correctamente');
          this.checkSelect = [];
          this.executeSelect = [];
        }
      });
    } else {
      this.selection.clear();
      this.selection2.clear();
      this.checkSelect = [];
      this.executeSelect = [];
    }



  }
  getListGarante() {
    this.catalogoService.listaGarante().subscribe(
      value => {
        this.garantes = value.response.data
      }
    )
  }

  validLoteDesde(e: any) {
    this.form.controls.loteHasta.setValidators([
      Validators.required
    ])
    this.form.controls.loteHasta.updateValueAndValidity();
  }

  validLoteHasta(e: any) {
    this.form.controls.loteDesde.setValidators([
      Validators.required
    ])
    this.form.controls.loteDesde.updateValueAndValidity();
  }

  clearForm() {
    this.form.reset();
    this.dataSource = new MatTableDataSource([]);
    this.messaje = 'Debe seleccionar un garante para iniciar la búsqueda.';
  }

  setGarante(event) {
    let tempGarante: any = this.garantes;
    let data = tempGarante.find(value => value.codigo == event.value)
    this.form.controls.garante.setValue(data.descripcion);
  }
  setGaranteTransporte(garanteId) {
    let tempGarante: any = this.garantes;
    let data = tempGarante.find(value => value.codigo == garanteId)
    this.form.controls.garante.setValue(data.descripcion);

  }
  public handlePage(e: any) {
    this.page = e.pageIndex + 1;
    this.pageSize = e.pageSize;
    this.dataSource = new MatTableDataSource([]);
    this.filtro();
  }
  goBack() {
    this.atrasboolean = true
    this.page = this.page - 1;
    this.siguienteApi = this.page == 1 ? '' : this.pagination[this.page].siguiente;
    this.filtro();
  }

  goForward() {
    this.page = this.page + 1;
    this.siguienteApi = this.pagination[this.page].siguiente;
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

  dellpage() {
    this.pagination = [];
    this.page = 1;
    this.atrasApi = null;
    this.siguienteApi = null;
  }
}
