import { DatePipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { ENPOINT_LOTE } from '../../utils/apis';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
  providedIn: 'root'
})
export class CatalogoService {
  nroLote: number = 0;
  garateId: number = 0;
  constructor(
    private _request: HttpRequestService,
    private pipe: DatePipe,
  ) { }


  transportLote() {
    return this.nroLote;
  }
  transporteGarante() {
    return this.garateId;
  }

  private gestioLoteSubject = new Subject<any>();
  entregaNroLote = this.gestioLoteSubject.asObservable();
  enviarNroLote(data: any) {
    this.gestioLoteSubject.next(data);
  }

  getTypeDocuments(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  getTypeFiles(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  getServices(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  getBenefits(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  getPrivilegios(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  getSedes(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
  listaGarante(): Observable<any> {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_LOTE.GARANTE_LIST;
    parametro.request = 'GET';
    return this._request.http(parametro);
  }
  getSede(): Observable<any> {
    const parametro: Parameter = new Parameter();
    parametro.url = ENPOINT_LOTE.SEDE_LIST;
    parametro.request = 'GET';
    return this._request.http(parametro);
  }

  listaEstatus() {
    return [
      { id: "TERMINADO", value: 'Terminado' },
      { id: "EN_PROCESO", value: 'En proceso' },
      { id: "PENDIENTE", value: 'Pendiente' }
    ]
  }
  listaEstatusMonitoreo() {
    return [
      { id: "PENDIENTE", value: 'Pendiente' },
      { id: "EN_PROCESO", value: 'En proceso' },
      { id: "TERMINADO", value: 'Terminado' },
      { id: "ERROR", value: 'Error' },
    ]
  }
  listaEstatusGenerarExpediente() {
    return [
      { id: "COMPLETO", value: 'Completo' },
      { id: "INCOMPLETO", value: 'Incompleto' }
    ]
  }
  listaEstatusEnviadoGarante() {
    return [
      { id: "PENDIENTE", value: 'PENDIENTE' },
      { id: "ENVIADO", value: 'ENVIADO' },
      { id: "ACEPTADO", value: 'ACEPTADO' },
      { id: "RECHAZADO", value: 'RECHAZADO' }
    ]
  }
  itemArrayNumber() {
    return [
      {
        code: 5,
        name: 5
      },
      {
        code: 10,
        name: 10
      },
      {
        code: 25,
        name: 25
      },
      {
        code: 100,
        name: 100
      }
    ];
  }
  itemInitial() {
    return 10;
  }

  formaDate(fecha: any) {
    const date = new Date(fecha);
    date.setDate(date.getDate());
    return this.pipe.transform(date, 'yyyy-MM-dd');

  }
 
}
// dashboard generado por mecanismo y modo de facturacion- SPRINT 3