import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
  providedIn: 'root'
})
export class    UsersService {

  constructor(
    private _request: HttpRequestService) { }


  getUsers(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
  getRoles(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  createUser(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  updateUser(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
  getRolUser(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
}
