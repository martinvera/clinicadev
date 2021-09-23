import { DatePipe } from '@angular/common';
import { ApplicationRef, Component, Inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { Garante } from 'src/app/shared/model/common/garante';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { Sede } from 'src/app/shared/model/entity/sede';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { GestionService } from 'src/app/shared/services/clinica-internacional/gestion.service';
import { ENDPOINT_DOCUMENTS, ENDPOINT_USERS } from 'src/app/shared/utils/apis';


interface Documents {
  codigo: string;
  descripcion: string;
}

interface Files {
  codigo: string;
  descripcion: string;
}

interface Services {
  codigo: string;
  descripcion: string;
  tipodocumento: any;
}

interface Benefits {
  codigo: string;
  descripcion: string;
}

@Component({
  selector: 'app-modal-edit',
  templateUrl: './modal-edit.component.html',
  styleUrls: ['./modal-edit.component.scss']
})
export class ModalEditComponent implements OnInit {
  dataArray: FormArray;
  uploadForm: FormGroup;
  formError: boolean = false;
  dellSuscribe: Subscription = new Subscription();
  descripService: any;
  descriptionDocumentUser: any;
  loadedDocumentsDesc: any = [];
  nameFile: any = [];
  base64: any = [];
  selectOption: any = [];
  docDataArchivos: any;
  dateDoc: any;
  nrEncuentro: any;
  loadedSedes: Sede[];
  garantes: Garante[];
  tempTipoDoc: any;

  constructor(
    public dialogRef: MatDialogRef<ModalEditComponent>,
    private fb: FormBuilder,
    private appRef: ApplicationRef,
    private catalogoService: CatalogoService,
    private gestionService: GestionService,
    private pipe: DatePipe,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  loadedDocuments: Documents[];
  loadedFiles: Files[];
  loadedServices: Services[];
  loadedBenefits: Benefits[];


  ngOnInit() {
    this.docDataArchivos = this.data.archivos;
    this.onCreateForm();
    this.onClearItems();
    this.getAllSelects();
    this.setValuesArrayInputs();
    this.updateHiddenParam(this.docDataArchivos);
    this.getListGarante();
  }

  onClickNO(): void {
    this.dialogRef.close(false);
  }

  get formdate() {
    return this.uploadForm.get('fechaAtencion') as FormArray;
  }

  get formarchivos() {
    return this.uploadForm.get('archivos') as FormArray;
  }

  onCreateForm() {

    this.tempTipoDoc = this.data.codigoServicioOrigen;
    this.uploadForm = this.fb.group({
      fechaAtencion: new FormControl('', Validators.required),
      nroHcPaciente: new FormControl(this.data.peticionHisID, Validators.required),
      apellidoPaterno: new FormControl(this.data.pacienteApellidoPaterno, Validators.required),
      apellidoMaterno: new FormControl(this.data.pacienteApellidoMaterno, Validators.required),
      nombrePaciente: new FormControl(this.data.pacienteNombre, Validators.required),
      tipoDocumento: new FormControl(this.data.pacienteTipoDocIdentId, Validators.required),
      pacienteID: new FormControl(this.data.pacienteNroDocIdent, Validators.required),
      servicio: new FormControl(this.data.codigoServicioOrigen, Validators.required),
      beneficio: new FormControl(this.data.beneficio, Validators.required),
      tipoDocumentoDescUser: new FormControl(this.data.pacienteTipoDocIdentDesc, Validators.required),
      descriptionService: new FormControl(this.data.descripcionServSolicita, Validators.required),
      sede: new FormControl(this.data.sede, Validators.required),
      garanteId: new FormControl(this.data.garanteId, Validators.required),
      garante: new FormControl(this.data.garanteDescripcion, Validators.required),
      archivos: new FormArray([this.FormDocumentosArray()])

    });
  }

  FormDocumentosArray() {

    return this.fb.group({
      archivoBytes: ['', { disabled: true }], //varAteFinal
      error: ['', { disabled: true }], //varAteFinal
      estadoArchivo: ['', { disabled: true }], //varAteFinal
      existe: ['', { disabled: true }], //varAteFinal
      msjError: ['', { disabled: true }], //varAteFinal
      nombreArchivo: ['', { disabled: true }],
      nroEncuentro: ['', { disabled: true }],
      tipoDocumentoDesc: ['', { disabled: true }],
      tipoDocumentoId: ['', { disabled: true }], //varAteInicio
      urlArchivo: ['', { disabled: true }], //varAteInicio
    });
  }


  setValuesArrayInputs() {
    this.data.archivos.map((elemnt: any) => {
      this.dataArray = this.formarchivos;
      this.dataArray.push(this.FormDocumentosArrayLleno(elemnt));
    });

  }


  FormDocumentosArrayLleno(data_array: any) {
    return this.fb.group({
      archivoBytes: [data_array.archivoBytes, { disabled: true }], //varAteFinal
      error: [data_array.error, { disabled: true }], //varAteFinal
      estadoArchivo: [data_array.estadoArchivo, { disabled: true }], //varAteFinal
      existe: [data_array.existe, { disabled: true }], //varAteFinal
      msjError: [data_array.msjError, { disabled: true }], //varAteFinal
      nombreArchivo: [data_array.nombreArchivo, { disabled: true }],
      nroEncuentro: [data_array.nroEncuentro, { disabled: true }],
      tipoDocumentoDesc: [data_array.tipoDocumentoDesc, { disabled: true }],
      tipoDocumentoId: [data_array.tipoDocumentoId, { disabled: true }], //varAteInicio
      urlArchivo: [data_array.urlArchivo, { disabled: true }], //varAteInicio
    });

  }



  updateHiddenParam(data_array: any) {
    let dateArchivo = new Date(this.data.fechaAtencion);
    dateArchivo.setDate(dateArchivo.getDate() + 2);
    this.dateDoc = this.pipe.transform(dateArchivo, 'yyyy-MM-dd');
    this.descripService = this.data.descripcionServSolicita;
    this.descriptionDocumentUser = this.data.pacienteTipoDocIdentDesc;
    this.nrEncuentro = this.data.nroEncuentro;
    data_array.forEach((item: any, index: any) => {
      this.base64[index] = item.archivoBytes;
      this.nameFile[index] = item.nombreArchivo;
      this.loadedDocumentsDesc[index] = item.tipoDocumentoDesc;
    });
  }


  onAddDocument() {
    this.dataArray = this.uploadForm.get('archivos') as FormArray;
    this.dataArray.push(this.FormDocumentosArray());
  }

  onRemoveItem(item: any) {
    this.dataArray = this.formarchivos;
    this.dataArray.removeAt(item);
    this.nameFile.splice(item, 1);
    this.base64.splice(item, 1);
    this.loadedDocumentsDesc.splice(item, 1);
    this.appRef.tick();
  }

  onClearItems() {
    this.dataArray = this.formarchivos;
    this.dataArray.clear();
  }

  formReset() {
    this.uploadForm.reset();
    this.onClearItems();
  }


  onFileSelected(e: any, i: any) {
    let name_base_64: any;
    this.nameFile[i] = e[0].name;
    name_base_64 = (e[0].base64).split(',');
    this.base64[i] = name_base_64[1];

  }

  triggerFile(index: any) {
    let content2: HTMLElement = document.getElementById('file-' + index) as HTMLElement;
    content2.click();
  }

  getAllSelects() {
    this.getTypeDocuments();

    // this.getTypeFiles();

    this.getServices();

    this.getBenefits();

    this.getSedes();
  }
  getTypeDocuments() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.TYPE_DOCUMENT_LIST;
    parametro.data = '';
    parametro.request = 'GET';
    this.dellSuscribe.add(this.catalogoService.getTypeDocuments(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.loadedDocuments = data.response.data;
      }
    ));
  }
  // getTypeFiles() {
  //   const parametro: Parameter = new Parameter();
  //   parametro.url = ENDPOINT_DOCUMENTS.TIPO_DOCUMENTO;
  //   parametro.data = '';
  //   parametro.request = 'GET';
  //   this.dellSuscribe.add(this.catalogoService.getTypeFiles(parametro).pipe(
  //   ).subscribe(
  //     (data: any) => {
  //       this.loadedFiles = data.response.data;
  //     }
  //   )
  //   );
  // }
  getServices() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_DOCUMENTS.SERVICIOS;
    parametro.data = '';
    parametro.request = 'GET';
    this.dellSuscribe.add(this.catalogoService.getServices(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.loadedServices = data.response.data;
        let da = this.loadedServices.find(x => { if (x.codigo == this.tempTipoDoc) { return x.tipodocumento } });
        this.loadedFiles = da?.tipodocumento;
      }
    ));
  }

  getBenefits() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_DOCUMENTS.BENEFICIOS;
    parametro.data = '';
    parametro.request = 'GET';
    this.dellSuscribe.add(this.catalogoService.getBenefits(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.loadedBenefits = data.response.data;
      }
    ));
  }

  getListGarante() {

    this.catalogoService.listaGarante().subscribe(
      value => {
        this.garantes = value.response.data;
      }
    )
  }

  obtenerencuentro(data: any) {
    this.nrEncuentro = data.target.value;
  }

  isCreate() {
    let createDocument = this.uploadForm.value;
    const date = new Date(this.formdate.value);
    date.setDate(date.getDate());


    const document_data = {
      header: {
        transactionId: "",
        applicationId: "",
        userId: ""
      },
      request: {
        nroEncuentro: this.data.nroEncuentro,
        peticionHisID: createDocument.nroHcPaciente,
        pacienteTipoDocIdentId: createDocument.tipoDocumento,
        pacienteNroDocIdent: createDocument.pacienteID,
        codigoServicioOrigen: createDocument.servicio,
        codigoServSolicita: createDocument.servicio,
        origenServicio: createDocument.descriptionService,
        pacienteApellidoPaterno: createDocument.apellidoPaterno,
        pacienteNombre: createDocument.nombrePaciente,
        pacienteApellidoMaterno: createDocument.apellidoMaterno,
        fechaAtencion: createDocument.fechaAtencion,
        beneficio: createDocument.beneficio,
        pacienteTipoDocIdentDesc: createDocument.tipoDocumentoDescUser,
        sede: createDocument.sede,
        garanteDescripcion: createDocument.garante,
        garanteId: createDocument.garanteId,
        archivos: createDocument.archivos,
        facturaNro: this.data.facturaNro,
        nroLote: this.data.nroLote
      }

    };

    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_DOCUMENTS.UPDATE_DOCUMENTO;
    parametro.data = document_data;
    parametro.request = 'PUT';
    this.dellSuscribe.add(this.gestionService.updateDocument(parametro).pipe(

    ).subscribe(
      async (value) => {
        this.dialogRef.close(value);

      },
    ));

  }

  onSelectOption(e: any, i: any) {
    this.loadedDocumentsDesc[i] = e.source.triggerValue;

  }


  selectFunctionService(e: any) {
    this.descripService = e.source.triggerValue;
  }

  selectFunctionTipoDocumentUser(e: any) {
    this.descriptionDocumentUser = e.target.options[e.target.selectedIndex].text;
  }
  getSedes() {
    this.dellSuscribe.add(this.catalogoService.getSede().pipe(
    ).subscribe(
      (data: any) => {
        this.loadedSedes = data.response.data;
      }
    ));
  }
  setGarante(event) {
    let tempGarante: any = this.garantes;
    let data = tempGarante.find(value => value.codigo == event.value)
    this.uploadForm.controls.garante.setValue(data.descripcion);
  }
}
