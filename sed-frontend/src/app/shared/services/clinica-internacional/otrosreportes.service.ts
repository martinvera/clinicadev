import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
    providedIn: 'root'
})
export class OtrosReportesService {

    constructor(private _request: HttpRequestService) { }

    getOtrosReportes(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
}
