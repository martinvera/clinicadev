import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
  providedIn: 'root'
})
export class GestionService {

  constructor(private _request: HttpRequestService) { }
  getDocuments(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  getExpedt(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  getExpedienteList(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  generarExpediente(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
  findDoc(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  getServices(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
  getBenefits(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
  updateDocument(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
  listDocument(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }



}
