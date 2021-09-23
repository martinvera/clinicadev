import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
    providedIn: 'root'
})
export class LoteService {

    constructor(private _request: HttpRequestService) { }

    getLote(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
    deleteLote(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
    reprocesarLote(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
}
