import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
    providedIn: 'root'
})
export class EnviadoGarante {

    constructor(private _request: HttpRequestService) { }

    getLote(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
    setStateEnviadoGarante(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
    getLogEnviadoGarante(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
    getDashboardEnviadoGarante(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
    setReportEnviadosGarante(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
}
