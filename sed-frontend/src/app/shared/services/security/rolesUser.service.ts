import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { HttpRequestService } from '../common/http-request.service';

@Injectable({
    providedIn: 'root'
})
export class roService {

    constructor(
        private _request: HttpRequestService
    ) { }

    getTypeDocuments(parametro: Parameter): Observable<any> {
        return this._request.http(parametro);
    }
}