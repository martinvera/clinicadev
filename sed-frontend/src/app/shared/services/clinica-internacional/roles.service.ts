import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
  providedIn: 'root'
})
export class RolesService {

  constructor(private _request: HttpRequestService) { }

  getRoles(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  createRoles(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  getPrivilegios(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  updateRoles(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
  getRolId(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
}
