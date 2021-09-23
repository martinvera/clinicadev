import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
  providedIn: 'root'
})
export class DocumentsService {

  constructor(
    private _request: HttpRequestService
  ) { }

  createDocument(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
  getDocument(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }
}
