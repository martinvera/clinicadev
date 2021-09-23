import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { RolesService } from 'src/app/shared/services/clinica-internacional/roles.service';
import { ENDPOINT_USERS } from 'src/app/shared/utils/apis';

interface Privilegios {
  nombre: string,
  descripcion: string,
  codigo: number,
}
@Component({
  selector: 'app-crear-rol',
  templateUrl: './crear-rol.component.html',
  styleUrls: ['./crear-rol.component.scss']
})
export class CrearRolComponent implements OnInit {
  createform: FormGroup;
  formError: boolean = false;

  state_modal: boolean = false;

  dellSuscribe: Subscription = new Subscription();

  //validacion de campo obligatorio
  nombre = new FormControl('', [
    Validators.required,
  ]);

  privilegios = new FormControl('', [
    Validators.required,
  ]);

  descripcion = new FormControl('', [
    Validators.required,
  ]);

  constructor(
    public dialogRef: MatDialogRef<CrearRolComponent>,
    private roleService: RolesService,
    @Inject(MAT_DIALOG_DATA) public contentRole: any) { }


  loadPriv: Privilegios[];
  title__modal: string;
  button__txt: string;

  ngOnInit() {
    this.getListPrivilegios();

    if (this.contentRole !== null) {
      this.title__modal = 'Actualizar Rol';
      this.button__txt = 'ACTUALIZAR';
      this.state_modal = true;
      this.UpdateForm();
    } else {
      this.title__modal = 'Crear Rol';
      this.button__txt = 'GUARDAR';
      this.state_modal = false;
      this.createForm();
    }
  }


  getListPrivilegios() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.LIST_PRIVILEGIOS + '?size=200';
    parametro.request = 'GET';
    this.roleService.getPrivilegios(parametro).pipe(
    ).subscribe((data: any) => {
      this.loadPriv = data.response.data;
    });
  }


  onclickNO(): void {
    this.dialogRef.close();
  }


  createForm() {
    this.createform = new FormGroup({
      nombre: new FormControl('', Validators.required),
      privilegios: new FormControl('', Validators.required),
      estado: new FormControl('', Validators.required),
      descripcion: new FormControl('', Validators.required),
    });
  }

  UpdateForm() {
    this.createform = new FormGroup({
      nombre: new FormControl(this.contentRole.nombre, Validators.required),
      privilegios: new FormControl((this.contentRole.privilegios).split(','), Validators.required),
      estado: new FormControl(parseInt(this.contentRole.estado), Validators.required),
      descripcion: new FormControl(this.contentRole.descripcion, Validators.required),
    });
  }


  submitButton() {
    if (this.contentRole !== null) {
      this.isUpdate();
    } else {
      this.isCreate();
    }
  }

  isCreate() {
    let createRoles = this.createform.value;
    const role_data = {
      codigo: '',
      nombre: createRoles.nombre.toUpperCase(),
      privilegios: (createRoles.privilegios).toString(),
      proveedor: createRoles.proveedor,
      estado: parseInt(createRoles.estado),
      descripcion: createRoles.descripcion.toUpperCase(),
    };

    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.CREATE_ROLE;
    parametro.data = role_data;
    parametro.request = 'POST';
    this.dellSuscribe.add(this.roleService.createRoles(parametro).pipe(
    ).subscribe(
      async (value) => {
        this.dialogRef.close(value);
        this.createform.reset();
      },
    ));
  }

  isUpdate() {
    let createRoles = this.createform.value;
    const role_data = {
      codigo: this.contentRole.codigo,
      nombre: createRoles.nombre,
      proveedor: createRoles.proveedor,
      estado: parseInt(createRoles.estado),
      privilegios: (createRoles.privilegios).toString(),
      descripcion: createRoles.descripcion,
    };
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.UPDATE_ROLE;
    parametro.data = role_data;
    parametro.request = 'PUT';
    this.dellSuscribe.add(this.roleService.updateRoles(parametro).pipe(
    ).subscribe(
      async (value) => {
        this.dialogRef.close(value);
        this.createform.reset();
      },
    ));

  }
}
