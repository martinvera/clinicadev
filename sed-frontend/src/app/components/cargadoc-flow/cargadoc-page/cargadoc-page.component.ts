import { DatePipe } from '@angular/common';
import { ApplicationRef, Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Garante } from 'src/app/shared/model/common/garante';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { DocumentsService } from 'src/app/shared/services/clinica-internacional/documents.service';
import { AuthService } from 'src/app/shared/services/security/auth.service';
import { ENDPOINT_DOCUMENTS, ENDPOINT_USERS, ENPOINT_LOTE } from 'src/app/shared/utils/apis';


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

interface Sedes {
  codigo: string;
  descripcion: string;

}

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}






export const MY_FORMATS = {
  parse: {
    dateInput: 'LL'
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'YYYY'
  }
};

@Component({
  selector: 'app-cargadoc-page',
  templateUrl: './cargadoc-page.component.html',
  styleUrls: ['./cargadoc-page.component.scss'],


})
export class CargadocPageComponent implements OnInit {

  //validacion de campos obligatorios
  nroEncuentro = new FormControl('', [
    Validators.required,
  ]);
  nroHcPaciente = new FormControl('', [
    Validators.required,
  ]);

  nombrePaciente = new FormControl('', [
    Validators.required,
  ]);
  apellidoPaterno = new FormControl('', [
    Validators.required,
  ]);



  loadedDocuments: Documents[];
  loadedFiles: Files[];
  loadedServices: Services[];
  loadedBenefits: Benefits[];
  loadedSedes: Sedes[];

  dataArray: FormArray;
  uploadForm: FormGroup;
  formError: boolean = false;
  dellSuscribe: Subscription = new Subscription();
  descripService: any;
  descriptionDocumentUser: any;
  nameFile: any = [];
  base64: any = [];
  loadedDocumentsDesc: any = [];
  nrEncuentro: any;
  disabledApePaterno: boolean;
  permission = Permission;


  garantes: Garante[];

  constructor(
    private fb: FormBuilder,
    private appRef: ApplicationRef,
    private catalogoService: CatalogoService,
    private documentService: DocumentsService,
    private toastrService: ToastrService,
    private pipe: DatePipe,
    private userService: AuthService,
  ) {
    this.disabledApePaterno = false;
  }

  tipoDocumentoId = new FormControl('valid', [
    Validators.required,
    Validators.pattern('valid'),
  ]);

  matcher = new MyErrorStateMatcher();


  ngOnInit(): void {
    this.getAllSelects();
    this.onCreateForm();
    this.getListGarante();
  }

  onCreateForm() {
    this.uploadForm = this.fb.group({
      fechaAtencion: new FormControl(''),
      nroEncuentro: new FormControl('', Validators.required),
      nroHcPaciente: new FormControl('', Validators.required),
      nombrePaciente: new FormControl('', Validators.required),
      apellidoPaterno: new FormControl(''),
      apellidoMaterno: new FormControl(''),
      tipoDocumento: new FormControl(''),
      tipoDocumentoDescUser: new FormControl(''),
      pacienteID: new FormControl(''),
      servicio: new FormControl('', Validators.required),
      beneficio: new FormControl(''),
      sede: new FormControl(''),
      codigoSede: new FormControl(''),
      descriptionService: new FormControl(''),
      archivos: new FormArray([this.FormDocumentosArray()]),
      garante: new FormControl('',  Validators.required),
      garanteId: new FormControl('',  Validators.required),
      beneficioDesc: new FormControl('')
    });
  }



  obtenerencuentro(data: any) {
    this.nrEncuentro = data.target.value;
  }

  onSelectOption(e: any, i: any) {
    let tempData = this.loadedFiles;
    let data = tempData.find(value => value.codigo == e.value)
    if (data.descripcion == 'OTROS') {

      (document.getElementById('nameOtros-' + i) as HTMLElement).style.display = "block";
      this.loadedDocumentsDesc[i] = '';

    } else {
      (document.getElementById('nameOtros-' + i) as HTMLElement).style.display = "none";
      this.loadedDocumentsDesc[i] = e.source.triggerValue;
    }
  }

  submitButton() {
    const dataDocumentos = this.uploadForm.get('archivos')?.value;
    let count = 0;
    if (dataDocumentos.length > 0) {
      dataDocumentos.map((value: any) => {
        if (value.archivoBytes == undefined || value.archivoBytes == '') {
          count++;
        }
      });

      if (count == 0) {
        this.isCreate();

      } else {
        if (count > 0) {
          this.toastrService.error(':: Debe subir algún documento.');
        }
      }
    }

  }

  FormDocumentosArray() {
    return this.fb.group({

      tipoDocumentoId: ['', { disabled: true }], //varAteInicio
      archivoBytes: ['', { disabled: true }], //varAteFinal
      nombreArchivo: ['', { disabled: true }],
      nroEncuentro: ['', { disabled: true }],
      tipoDocumentoDesc: ['', { disabled: true }],
      otros: ['', { disabled: true }],
    });
  }


  onAddDocument() {
    this.dataArray = this.uploadForm.get('archivos') as FormArray;
    this.dataArray.push(this.FormDocumentosArray());
  }

  onRemoveItem(item: any) {
    this.dataArray = this.uploadForm.get('archivos') as FormArray;
    this.dataArray.removeAt(item);
    this.nameFile.splice(item, 1);
    this.base64.splice(item, 1);
    this.loadedDocumentsDesc.splice(item, 1);
    this.appRef.tick();
  }

  onClearItems() {
    this.dataArray = this.uploadForm.get('archivos') as FormArray;
    this.dataArray.clear();
  }

  formReset() {
    this.uploadForm.reset();
    this.onClearItems();
  }

  get docFromGroupArchivos() {
    return this.uploadForm.get('archivos') as FormArray;
  }

  get docFromGroupDates() {
    return this.uploadForm.get('fechaAtencion') as FormArray;
  }



  getAllSelects() {
    this.getTypeDocuments();
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

  getServices() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_DOCUMENTS.SERVICIOS;
    parametro.data = '';
    parametro.request = 'GET';
    this.dellSuscribe.add(this.catalogoService.getServices(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.loadedServices = data.response.data;
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

  getSedes() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_LOTE.SEDE_LIST;
    parametro.request = 'GET';
    this.dellSuscribe.add(this.catalogoService.getSedes(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.loadedSedes = data.response.data;
      }
    ));
  }

  //garante
  getListGarante() {

    this.catalogoService.listaGarante().subscribe(
      value => {
        this.garantes = value.response.data
      }
    )
  }

  isCreate() {
    let createDocument = this.uploadForm.value;
    if (this.disabledApePaterno){
      createDocument.apellidoPaterno = 'NO TIENE APELLIDO PATERNO'  }
    let fechaActual: any;
    if (this.docFromGroupDates.value !== '') {
      const date = new Date(this.docFromGroupDates.value);
      date.setDate(date.getDate());
      fechaActual = this.pipe.transform(date, 'yyyy-MM-dd');
    } else {
      fechaActual = '';
    }


    const document_data = {
      header: {
        transactionId: "",
        applicationId: "",
        userId: "",
        userName: this.userService.getUser()
      },
      request: {
        nroEncuentro: createDocument.nroEncuentro,
        peticionHisID: createDocument.nroHcPaciente,
        pacienteTipoDocIdentId: createDocument.tipoDocumento,
        pacienteNroDocIdent: createDocument.pacienteID,
        codigoServicioOrigen: createDocument.servicio,
        origenServicio: createDocument.descriptionService,
        pacienteApellidoPaterno: createDocument.apellidoPaterno.toUpperCase(),
        pacienteNombre: createDocument.nombrePaciente.toUpperCase(),
        pacienteApellidoMaterno: createDocument.apellidoMaterno.toUpperCase(),
        fechaAtencion: fechaActual,
        beneficio: createDocument.beneficio,
        pacienteTipoDocIdentDesc: createDocument.tipoDocumentoDescUser,
        sede: createDocument.sede,
        codigoSede: createDocument.codigoSede,
        archivos: createDocument.archivos,
        garanteDescripcion: createDocument.garante,
        garanteId: createDocument.garanteId,
        beneficioDesc: createDocument.beneficioDesc,
        userName: this.userService.getUser().toUpperCase()
      }

    };

    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_DOCUMENTS.CREATE_DOCUMENT;
    parametro.data = document_data;
    parametro.request = 'POST';
    this.dellSuscribe.add(this.documentService.createDocument(parametro).pipe(
    ).subscribe(
      (value: any) => {
        if (value.response.code == 200 && value.response.data) {
          this.uploadForm.reset();
          this.onClearItems();
          this.toastrService.success(value.response.data);
        } else {
          this.toastrService.error(value.response.data);
        }
      }
    ));

  }

  onFileSelected(event: any, i: any) {
    let name_base_64: any;
    this.nameFile[i] = event[0].name;
    name_base_64 = (event[0].base64).split(',');
    this.base64[i] = name_base_64[1];
  }

  triggerFile(index: any) {
    let content2: HTMLElement = document.getElementById('input-file-' + index);
    content2.click();
  }

  selectFunctionService(e: any) {
    this.loadedFiles = [];
    this.descripService = e.source.triggerValue;
    let da = this.loadedServices.find(x => { if (x.codigo == e.source.value) { return x.tipodocumento } })
    this.loadedFiles = da?.tipodocumento;
  }

  selectFunctionTipoDocumentUser(e: any) {
    this.descriptionDocumentUser = e.source.triggerValue;
  }
  dissabletMaterno() {
    this.disabledApePaterno = !this.disabledApePaterno;
    if (this.disabledApePaterno) {
      this.uploadForm.controls.apellidoPaterno.clearValidators();
      this.uploadForm.controls.apellidoPaterno.updateValueAndValidity();
      this.uploadForm.controls.apellidoPaterno.setValidators([]);
      this.uploadForm.controls.apellidoPaterno.disable();
      this.uploadForm.controls.apellidoPaterno.setValue(null);
    }
    else {
      this.uploadForm.controls.apellidoPaterno.enable();
      this.uploadForm.controls.apellidoPaterno.setValue(null);
      this.uploadForm.controls.apellidoPaterno.setValidators([Validators.required]);
      this.uploadForm.controls.apellidoPaterno.updateValueAndValidity();
    }
  }
  
  setGarante(event) {
    let tempGarante: any = this.garantes;
    let data = tempGarante.find(value => value.codigo == event.value)
    this.uploadForm.controls.garante.setValue(data.descripcion);

  }
  selectFunctionSede(event) {
    let tempSede: any = this.loadedSedes;
    let data = tempSede.find(value => value.codigo == event.value)
    this.uploadForm.controls.sede.setValue(data.descripcion);
  }
  setBeneficio(event) {
    let tempBeneficios: any = this.loadedBenefits;
    let data = tempBeneficios.find(value => value.codigo == event.value)
    this.uploadForm.controls.beneficioDesc.setValue(data.descripcion);
  }
}