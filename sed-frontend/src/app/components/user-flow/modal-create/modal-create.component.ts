import { Component, Inject, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { UsersService } from 'src/app/shared/services/clinica-internacional/users.service';
import { ENDPOINT_USERS } from 'src/app/shared/utils/apis';

interface Documents {
  descripcion: string;
}

interface Roles {
  codigo: string | number;
  nombre: string;
}

@Component({
  selector: 'app-modal-create',
  templateUrl: './modal-create.component.html',
  styleUrls: ['./modal-create.component.scss']
})
export class ModalCreateComponent implements OnInit {
  createform: FormGroup;
  formError: boolean = false;
  dellSuscribe: Subscription = new Subscription();
  @Input() dataUserItem: any;
  userName: string | undefined;
  visible = false;
  inputType = 'password';
  //validaciones de campos obligatorios
  tipoDocumento = new FormControl('', [
    Validators.required,
  ]);

  numeroDocumento = new FormControl('', [
    Validators.required,
  ]);

  nombres = new FormControl('', [
    Validators.required,
  ]);

  apellidoPaterno = new FormControl('', [
    Validators.required,
  ]);

  apellidoMaterno = new FormControl('', [
    Validators.required,
  ]);

  cargo = new FormControl('', [
    Validators.required,
  ]);

  telefono = new FormControl('', [

  ]);

  role = new FormControl('', [
    Validators.required,
  ]);

  username = new FormControl('', [
    Validators.required,
  ]);

  userEmail = new FormControl('', [

  ]);

  password = new FormControl('', [
    Validators.required,
  ]);

  proveedor = new FormControl('', [

  ]);


  constructor(
    public dialogRef: MatDialogRef<ModalCreateComponent>,
    private userService: UsersService,
    private catalogoService: CatalogoService,
    @Inject(MAT_DIALOG_DATA) public contentUser: any) { }


  loadedRoles: Roles[];
  loadedDocuments: Documents[];

  title__modal: string;
  button__txt: string;

  ngOnInit() {
    this.getListRoles();
    this.getTypeDocument();


    if (this.contentUser !== null) {
      this.UpdateForm();
      this.userName = this.createform.get('username')?.value;
      this.createform.controls.username.disable();
      this.title__modal = 'Actualizar Usuario';
      this.button__txt = 'ACTUALIZAR';
    } else {
      this.createForm();
      this.title__modal = 'Crear Usuario';
      this.button__txt = 'GUARDAR';
    }
  }
  getTypeDocument() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.TYPE_DOCUMENT_LIST;
    parametro.request = 'GET';
    this.catalogoService.getTypeDocuments(parametro).pipe().subscribe((data: any) => {
      this.loadedDocuments = data.response.data;
    });
  }

  getListRoles(): void {
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.ROLLIST + '?size=1000';
    parametro.request = 'GET';
    this.userService.getRoles(parametro).pipe(
    ).subscribe((data: any) => {
      this.loadedRoles = data.response.data;
    });

  }

  createForm() {
    this.createform = new FormGroup({
      tipoDocumento: new FormControl('', Validators.required),
      numeroDocumento: new FormControl('', Validators.required),
      estado: new FormControl('', Validators.required),

      nombres: new FormControl('', Validators.required),
      apellidoPaterno: new FormControl('', Validators.required),
      apellidoMaterno: new FormControl('', Validators.required),
      cargo: new FormControl('', Validators.required),
      role: new FormControl('', Validators.required),
      username: new FormControl('', Validators.required),
      userEmail: new FormControl(''),
      password: new FormControl('', Validators.required),
      telefono: new FormControl(''),
      proveedor: new FormControl(''),
      nombreRol: new FormControl('', Validators.required),
    });
  }


  UpdateForm() {
    this.createform = new FormGroup({
      tipoDocumento: new FormControl(this.contentUser.tipoDocumento, Validators.required),
      numeroDocumento: new FormControl(this.contentUser.numeroDocumento, Validators.required),
      estado: new FormControl(this.contentUser.estado, Validators.required),
      nombres: new FormControl(this.contentUser.nombres, Validators.required),
      apellidoPaterno: new FormControl(this.contentUser.apellidoPaterno, Validators.required),
      apellidoMaterno: new FormControl(this.contentUser.apellidoMaterno, Validators.required),
      cargo: new FormControl(this.contentUser.cargo, Validators.required),
      role: new FormControl(this.contentUser.role, Validators.required),
      username: new FormControl(this.contentUser.username, Validators.required),
      userEmail: new FormControl(this.contentUser.userEmail, Validators.required),
      password: new FormControl(this.contentUser.password, Validators.required),
      telefono: new FormControl(this.contentUser.telefono, Validators.required),
      proveedor: new FormControl(this.contentUser.proveedor, Validators.required),
      nombreRol: new FormControl(this.contentUser.nombreRol, Validators.required),
    });
  }

  submitButton() {
    if (this.contentUser !== null) {
      this.isUpdate();
    } else {
      this.isCreate();
    }
  }

  isCreate() {
    let createUser = this.createform.value;
    const user_data = {
      nombres: createUser.nombres.toUpperCase(),
      apellidoPaterno: createUser.apellidoPaterno.toUpperCase(),
      apellidoMaterno: createUser.apellidoMaterno.toUpperCase(),
      cargo: createUser.cargo.toUpperCase(),
      tipoDocumento: createUser.tipoDocumento,
      numeroDocumento: createUser.numeroDocumento,
      telefono: createUser.telefono,
      username: createUser.username,
      userEmail: createUser.userEmail,
      password: createUser.password,
      role: createUser.role,
      estado: createUser.estado,
      proveedor: createUser.proveedor,
      nombreRol: createUser.nombreRol
    };
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.CREATE;
    parametro.data = user_data;
    parametro.request = 'POST';

    this.dellSuscribe.add(this.userService.createUser(parametro).pipe(
    ).subscribe(
      async (value: any) => {
        this.dialogRef.close(value);
        this.createform.reset();
      },
    ));

  }


  isUpdate() {
    let createUser = this.createform.value;
    const user_data = {
      nombres: createUser.nombres.toUpperCase(),
      apellidoPaterno: createUser.apellidoPaterno.toUpperCase(),
      apellidoMaterno: createUser.apellidoMaterno.toUpperCase(),
      cargo: createUser.cargo.toUpperCase(),
      tipoDocumento: createUser.tipoDocumento,
      numeroDocumento: createUser.numeroDocumento,
      telefono: createUser.telefono,
      username: this.userName,
      userEmail: createUser.userEmail,
      password: createUser.password,
      role: createUser.role,
      estado: (createUser.estado).toString(),
      proveedor: createUser.proveedor,
      nombreRol: createUser.nombreRol
    };

    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.UPDATE_USER;
    parametro.data = user_data;
    parametro.request = 'PUT';

    this.dellSuscribe.add(this.userService.updateUser(parametro).pipe(

    ).subscribe(
      async (value: any) => {
        this.dialogRef.close(value);
        this.createform.reset();
      },
    ));
  }

  onClickNO(): void {
    this.dialogRef.close(false);
    this.createform.reset();
  }
  setNameRol(event) {
    let tempRol: any = this.loadedRoles;
    let data = tempRol.find(value => value.codigo == event.value)
    this.createform.controls.nombreRol.setValue(data.nombre);
  }

  toggleVisibility() {
    if (this.visible) {
      this.inputType = 'password';
      this.visible = false;
      // this.cd.markForCheck();
    } else {
      this.inputType = 'text';
      this.visible = true;
      // this.cd.markForCheck();
    }
  }

}
