import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { Garante } from 'src/app/shared/model/common/garante';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { GeneradosEstado } from 'src/app/shared/model/entity/generadoEstado';
import { GeneradosOrigen } from 'src/app/shared/model/entity/generadoOrigen';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { ExpedienteGeneradoService } from 'src/app/shared/services/clinica-internacional/expedientes_generados.service';
import { ENPOINT_EXPEDIENTES_GENERADOS } from 'src/app/shared/utils/apis';

@Component({
  selector: 'app-dash-exp-generados-page',
  templateUrl: './dash-exp-generados-page.component.html',
  styleUrls: ['./dash-exp-generados-page.component.scss']
})
export class DashExpGeneradosPageComponent implements OnInit {
  dellSuscribe: Subscription = new Subscription();
  form: FormGroup;
  garantes: Garante[];
  generadosEstadoList: GeneradosEstado[];
  generadosOrigenList: GeneradosOrigen[];
  header = {
    fechaDesdeHasta: '',
    garanteDescripcion: '',
    garanteId: '',
    garante: '',
    lote: ''
  }
  constructor(
    private pipe: DatePipe,
    private expedientesGeneradosService: ExpedienteGeneradoService,
    private catalogoService: CatalogoService,
    private toastrService: ToastrService,
  ) {
    this.generadosEstadoList = [];
    this.generadosOrigenList = [];
  }

  ngOnInit(): void {
    this.onCreateFilter();
    this.getListGarante();
  }

  onCreateFilter() {
    this.form = new FormGroup({
      nroLote: new FormControl('', Validators.required),
      garante: new FormControl('', Validators.required),
      garanteDescripcion: new FormControl(''),
      fechaDesde: new FormControl('', Validators.required),
      fechaHasta: new FormControl('', Validators.required),
    });
  }

  openEvent(e: MatDatepickerInputEvent<Date>) {
    const fechaHasta = (<HTMLInputElement>document.getElementById('fechaHasta')).value;
    const today = new Date();
    if (fechaHasta == '') {
      this.form.get('fechaHasta')?.setValue(today);
    }
  }
  searchMecanismo() {

    
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_EXPEDIENTES_GENERADOS.EXPEDIENTES_GENERADOS;
    parametro.request = 'POST';
    parametro.data = {
      "header": {
        "transactionId": "",
        "applicationId": "",
        "userId": ""
      },
      "request": {
        "fechaDesde": this.form.value.fechaDesde !== "" && this.form.value.fechaDesde !== null ? this.catalogoService.formaDate(this.form.value.fechaDesde): null ,
        "fechaHasta": this.form.value.fechaHasta !== "" && this.form.value.fechaHasta !== null ? this.catalogoService.formaDate(this.form.value.fechaHasta): null,
        "nroLote": this.form.value.nroLote,
        "garanteId": this.form.value.garante,
        "garanteDescripcion": this.form.value.garanteDescripcion
      }
    }
    this.dellSuscribe.add(this.expedientesGeneradosService.searchExpedientegenerado(parametro).pipe(
    ).subscribe(
      (value: any) => {
        this.header.fechaDesdeHasta = value.response.data.fechaDesdeHasta;
        this.header.garanteDescripcion = value.response.data.garanteDescripcion
        this.header.garanteId = value.response.data.garanteDescripcion;
        this.header.lote = this.form.value.nroLote;



        if (value.response.data.generadosEstadoList.length > 0) {
          this.generadosEstadoList = value.response.data.generadosEstadoList;

        } else {
          this.generadosEstadoList = [];
          this.toastrService.info('No se encontró ningún registro para la gráfica de expedientes generados')
        }
        if (value.response.data.generadosOrigenList.length > 0) {
          this.generadosOrigenList = value.response.data.generadosOrigenList;
          this.generadosOrigenList = [];
        } else {
          this.toastrService.info('No se encontró ningún registro para la  gráfica de expedientes generados con error basados en su origen')
        }

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

  setGarante(event) {
    let tempGarante: any = this.garantes;
    let data = tempGarante.find(value => value.codigo == event.value);
    this.form.controls.garanteDescripcion.setValue(data.descripcion);

  }
}
