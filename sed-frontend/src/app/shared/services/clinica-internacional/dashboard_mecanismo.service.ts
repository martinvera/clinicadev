import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
    providedIn: 'root'
})
export class MecanismoService {

    constructor(
        private _request: HttpRequestService
    ) { }

    searchMecanismo(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
    generarReporte(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
}
