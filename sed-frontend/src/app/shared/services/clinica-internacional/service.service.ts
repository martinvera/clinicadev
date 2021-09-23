import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { ENPOINT_MECANISMOFACTURACION } from '../../utils/apis';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
    providedIn: 'root'
})
export class Service {

    constructor(private _request: HttpRequestService) { }

    getMecanismo(): Observable<any> {
        const parametro: Parameter = new Parameter();
        parametro.url = ENPOINT_MECANISMOFACTURACION.MECANISMOFACTURACION;
        parametro.request = 'GET';
        return this._request.http(parametro);
    }
}
