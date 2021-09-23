import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { Subscription } from 'rxjs';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { Garante } from 'src/app/shared/model/common/garante';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { EnviadosGarante } from 'src/app/shared/model/entity/enviadosGarante';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { EnviadoGarante } from 'src/app/shared/services/clinica-internacional/enviado_garante.service';
import { ENPOINT_ENVIADOS_GARANTE } from 'src/app/shared/utils/apis';

@Component({
  selector: 'app-dash-lote-enviado-garante-page',
  templateUrl: './dash-lote-enviado-garante-page.component.html',
  styleUrls: ['./dash-lote-enviado-garante-page.component.scss']
})
export class DashLoteEnviadoGarantePageComponent implements OnInit {
  permission = Permission;
  dellSuscribe: Subscription = new Subscription();
  form: FormGroup;
  garantes: Garante[];
  enviadosGarante: EnviadosGarante[];
  header = {
    fechaDesdeHasta: '',
    garanteDescripcion: '',
    garanteId: '',
    garante: '',
    lote: ''
  }
  constructor(
    private catalogoService: CatalogoService,
    private pipe: DatePipe,
    private enviadoGaranteService: EnviadoGarante
    
  ) {
    this.enviadosGarante = [];
  }

  ngOnInit(): void {
    this.onCreateFilter()
    this.getListGarante()
  }

  onCreateFilter() {
    this.form = new FormGroup({
      garanteId: new FormControl('', Validators.required),
      garanteDescripcion: new FormControl(''),
      fechaDesde: new FormControl('', Validators.required),
      fechaHasta: new FormControl('', Validators.required),
    });
  }
  setGarante(event) {
    let tempGarante: any = this.garantes;
    let data = tempGarante.find(value => value.codigo == event.value)
    this.form.controls.garanteDescripcion.setValue(data.descripcion);

  }
  getListGarante() {
    this.catalogoService.listaGarante().subscribe(
      value => {
        this.garantes = value.response.data
      }
    )
  }

  searchEnviadoGarante() {
   

    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_ENVIADOS_GARANTE.DASHBOARD_ENVIADOS_GARANTE;
    parametro.request = 'POST';
    parametro.data = {
      header: {
        transactionId: "",
        applicationId: "",
        userId: "",
      },
      "request": {
        "fechaLoteDesde": this.form.value.fechaDesde !== "" && this.form.value.fechaDesde !== null ? this.catalogoService.formaDate(this.form.value.fechaDesde): null,
        estado: 'TERMINADO',
        "fechaLoteHasta": this.form.value.fechaHasta !== "" && this.form.value.fechaHasta !== null ? this.catalogoService.formaDate(this.form.value.fechaHasta): null,
        "garanteId": this.form.value.garanteId,
        "garanteDescripcion": this.form.value.garanteDescripcion,
      }
    }
    this.enviadosGarante = [];

    this.header = parametro.data.request;
    this.dellSuscribe.add(this.enviadoGaranteService.getDashboardEnviadoGarante(parametro).pipe(
    ).subscribe(
      (data) => {
        this.enviadosGarante = data[0].estadoTotalList;
        this.header.garanteDescripcion = data[0].garanteDescripcion;
        this.header.fechaDesdeHasta = data[0].fechaDesdeHasta;
      }
    ));
  }

  clearForm() {
    this.form.reset();
    
  }
  openEvent(e: MatDatepickerInputEvent<Date>) {
    const fechaHasta = (<HTMLInputElement>document.getElementById('fechaHasta')).value;
    const today = new Date();
    if (fechaHasta == '') {
      this.form.get('fechaHasta')?.setValue(today);
    }
  }
}
