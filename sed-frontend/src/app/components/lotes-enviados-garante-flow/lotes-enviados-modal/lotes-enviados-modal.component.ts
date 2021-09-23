
import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { Estado } from 'src/app/shared/model/common/status';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { EnviadoGarante } from 'src/app/shared/services/clinica-internacional/enviado_garante.service';
import { AuthService } from 'src/app/shared/services/security/auth.service';
import { ENPOINT_ENVIADOS_GARANTE } from 'src/app/shared/utils/apis';

@Component({
  selector: 'app-lotes-enviados-modal',
  templateUrl: './lotes-enviados-modal.component.html',
  styleUrls: ['./lotes-enviados-modal.component.scss']
})
export class LotesEnviadosModalComponent implements OnInit {
  
  form: FormGroup;
  estados: Estado[];
  permission = Permission;
  messaje: string = 'Debe ingresar un lote inicial, lote final y garante para poder realizar una búsqueda.';
  disabletRegistroSolicitud:boolean;
  
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<LotesEnviadosModalComponent>,
    public dialog: MatDialog,
    private fb: FormBuilder,
    private enviadoGaranteService: EnviadoGarante,
    private userService: AuthService,
    private catalogoService: CatalogoService,
  ) {
    this.estados = this.catalogoService.listaEstatusEnviadoGarante();
    this.disabletRegistroSolicitud = false;
  }

  ngOnInit(): void {
    this.filtroForm();
    this.form.controls['fechaLote'].setValue(this.data.timestamp);
    this.form.controls['nroLote'].setValue(this.data.rowKey);
    this.form.controls['estado'].setValue(this.data.estadoGarante);
    this.form.controls['garanteId'].setValue(this.data.partitionKey);
    this.form.controls['fechaEnvio'].setValue(this.data.fechaEnvio);
    this.form.controls['fechaAceptacion'].setValue(this.data.fechaAceptacion);
    this.form.controls['fechaRechazo'].setValue(this.data.fechaRechazo);
    this.form.controls['registroSolicitud'].setValue(this.data.registroSolicitud);
    setTimeout(() => {
      this.onSelectOptionState(this.data.estadoGarante, 2);
    }, 500);
  }

  dialogClose() {
    this.dialogRef.close(false);
  }
  filtroForm() {
    this.form = this.fb.group({
      estado: [null],
      nroLote: [null],
      garanteId: [null],
      fechaLote: [null],
      fechaEnvio: [null],
      fechaAceptacion: [null],
      fechaRechazo: [null],
      usuario: [null],
      registroSolicitud: [null],
    });
  }
  setChangeState() {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_ENVIADOS_GARANTE.CHANGE_ESTADO_ENVIADOS_GARANTE;
    parametro.request = 'POST';
   

    parametro.data = {
      request: {
        estado: this.form.value.estado,
        fechaLote: this.form.value.fechaLote,
        fechaEnvio: this.form.value.fechaEnvio !== "" && this.form.value.fechaEnvio !== null && this.form.value.fechaEnvio !== "-" && this.form.value.fechaEnvio !== undefined && this.varChangeFechaEnvio == true ? this.catalogoService.formaDate(this.form.value.fechaEnvio): this.form.value.fechaEnvio,
        fechaAceptacion: this.form.value.fechaAceptacion !== "" && this.form.value.fechaAceptacion !== null && this.form.value.fechaAceptacion !== "-" && this.form.value.fechaAceptacion !== undefined && this.varChangeFechaAceptacion == true ? this.catalogoService.formaDate(this.form.value.fechaAceptacion):this.form.value.fechaAceptacion,
        registroSolicitud: this.form.value.registroSolicitud,
        nroLote: this.form.value.nroLote,
        fechaRechazo: this.form.value.fechaRechazo !== "" && this.form.value.fechaRechazo !== null && this.form.value.fechaRechazo !== undefined && this.form.value.fechaRechazo !== "-" && this.varFechaRechazo == true ? this.catalogoService.formaDate(this.form.value.fechaRechazo):this.form.value.fechaRechazo,
        garanteId: this.form.value.garanteId,
        userName: this.userService.getUser().toUpperCase()
      }
    }

    this.enviadoGaranteService.setStateEnviadoGarante(parametro).
      subscribe(
        async (value: any) => {
         
          if (value?.estado) {
            this.dialogRef.close(value);
          } 
        }
      )
  }
  onSelectOptionState(e: any, vari: number) {
    let value: string;
    if (vari == 1) {
      value = e.value
    } else {
      value = e;
    }
   
    if (value == 'PENDIENTE') {
      document.getElementById('idFechaEnvio1').style.display = "none";
      document.getElementById('idFechaEnvio2').style.display = "none";
      document.getElementById('idFechaAceptacion1').style.display = "none";
      document.getElementById('idFechaAceptacion2').style.display = "none";
      document.getElementById('idFechaRechazo1').style.display = "none";
      document.getElementById('idFechaRechazo2').style.display = "none";
      document.getElementById('idRegistroSolicitud1').style.display = "none";
      document.getElementById('idRegistroSolicitud2').style.display = "none";
      document.getElementById('temRegistroSolicitud2').style.display = "none";
    }
    if (value == 'ENVIADO') {
      document.getElementById('idFechaEnvio1').style.display = "block";
      document.getElementById('idFechaEnvio2').style.display = "block";
      document.getElementById('idFechaAceptacion1').style.display = "none";
      document.getElementById('idFechaAceptacion2').style.display = "none";
      document.getElementById('idFechaRechazo1').style.display = "none";
      document.getElementById('idFechaRechazo2').style.display = "none";
      document.getElementById('idRegistroSolicitud1').style.display = "block";
      document.getElementById('idRegistroSolicitud2').style.display = "block";
      document.getElementById('temRegistroSolicitud1').style.display = "none";
      document.getElementById('temRegistroSolicitud2').style.display = "none";
    }
    if (value == 'ACEPTADO') {
      document.getElementById('idFechaAceptacion1').style.display = "block";
      document.getElementById('idFechaAceptacion2').style.display = "block";
      document.getElementById('idFechaRechazo1').style.display = "none";
      document.getElementById('idFechaRechazo2').style.display = "none";
      document.getElementById('idRegistroSolicitud1').style.display = "none";
      document.getElementById('idRegistroSolicitud2').style.display = "none";
      document.getElementById('temRegistroSolicitud').setAttribute('value', this.form.value.registroSolicitud);
      document.getElementById('temRegistroSolicitud1').style.display = "block";
      document.getElementById('temRegistroSolicitud2').style.display = "block";
    }
    if (value == 'RECHAZADO') {
      document.getElementById('idFechaAceptacion1').style.display = "none";
      document.getElementById('idFechaAceptacion2').style.display = "none";
      document.getElementById('idFechaRechazo1').style.display = "block";
      document.getElementById('idFechaRechazo2').style.display = "block";
      document.getElementById('idRegistroSolicitud1').style.display = "none";
      document.getElementById('idRegistroSolicitud2').style.display = "none";

    }

  }
  varChangeFechaEnvio: boolean = false;
  varChangeFechaAceptacion: boolean = false;
  varFechaRechazo: boolean = false;
  changeFechaEnvio() {
    this.varChangeFechaEnvio = true;
  }
  changeFechaAceptacion() {
    this.varChangeFechaAceptacion = true;
  }
  changeFechaRechazo() {
    this.varFechaRechazo = true;
  }
}
