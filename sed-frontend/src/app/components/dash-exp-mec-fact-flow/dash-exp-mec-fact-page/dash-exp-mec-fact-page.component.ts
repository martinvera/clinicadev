import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { Garante } from 'src/app/shared/model/common/garante';
import { Parameter } from 'src/app/shared/model/common/parameter';
import { Mecanismo } from 'src/app/shared/model/entity/mecanismo';
import { MecanismoModoFact } from 'src/app/shared/model/entity/mecanismoModo';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';
import { MecanismoService } from 'src/app/shared/services/clinica-internacional/dashboard_mecanismo.service';
import { Service } from 'src/app/shared/services/clinica-internacional/service.service';
import { ENPOINT_MECANISMOFACTURACION } from 'src/app/shared/utils/apis';

@Component({
  selector: 'app-dash-exp-mec-fact-page',
  templateUrl: './dash-exp-mec-fact-page.component.html',
  styleUrls: ['./dash-exp-mec-fact-page.component.scss']
})
export class DashExpMecFactPageComponent implements OnInit {
  dellSuscribe: Subscription = new Subscription();
  form: FormGroup;
  mecanismos: Mecanismo[];
  mecanismoModoFact: MecanismoModoFact[];
  header: any;
  garantes: Garante[];
  constructor(
    private mecanismoService: Service,
    private dashboardMecanismo: MecanismoService,
    private pipe: DatePipe,
    private toastrService: ToastrService,
    private catalogoService: CatalogoService,
  ) {
    this.mecanismoModoFact = [];
  }

  ngOnInit(): void {
    this.onCreateFilter();
    this.getListaMecanismo();
  }

  onCreateFilter() {
    this.form = new FormGroup({
      mecanismo: new FormControl('', Validators.required),
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

  getListaMecanismo() {
    this.mecanismoService.getMecanismo().subscribe(
      value => {
        this.mecanismos = value.response.data;
      }
    )
  }

  searchMecanismo() {
    
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_MECANISMOFACTURACION.DASHBOARD_MECANISMO;
    parametro.request = 'POST';
    parametro.data = {
      header: {
        transactionId: "",
        applicationId: "",
        userId: "",
      },
      "request": {
        "fechaDesde": this.form.value.fechaDesde !== "" && this.form.value.fechaDesde !== null ? this.catalogoService.formaDate(this.form.value.fechaDesde): null,
        "fechaHasta": this.form.value.fechaHasta !== "" && this.form.value.fechaHasta !== null ? this.catalogoService.formaDate(this.form.value.fechaHasta): null,
        "tipoReporte": "EXPEDIENTEMECANISMO",
        "mecanismoFacturacionId": this.form.value.mecanismo
      }
    }
    this.header = parametro.data.request;
    this.dellSuscribe.add(this.dashboardMecanismo.searchMecanismo(parametro).pipe(
    ).subscribe(
      (data) => {
        if (data.length > 0) {
          this.mecanismoModoFact = data;
        } else {
          this.mecanismoModoFact = []
          this.toastrService.info('No se encontró ningún registro con los filtros ingresados.')
        }
      }
    ));
  }
  setGarante(event) {
    let tempGarante: any = this.garantes;
    let data = tempGarante.find(value => value.codigo == event.value)
    this.form.controls.garante.setValue(data.descripcion);
  }


}
