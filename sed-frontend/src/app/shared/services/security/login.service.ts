import { Injectable } from '@angular/core';
import { HttpRequestService } from '../common/http-request.service';
import { Observable } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private _request: HttpRequestService, private http: HttpClient) { }
  login(parametro: Parameter) {
    const apiUrl = environment.API_BASE + parametro.url
    const headers = new HttpHeaders({
      'Authorization': `Basic Y2xpbmljYS1pbnRlcm5hY2lvbmFsOnNlY3JldA==`
    });
    return this.http.post<any>(apiUrl, parametro.data, { headers, observe: 'response' })
  }

  login1(parametro: Parameter): Observable<any> {

    return this._request.http(parametro);
  }

  check(parametro: Parameter): Observable<any> {

    return this._request.http(parametro);
  }

  resetPassword(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }

  changePassword(parametro: Parameter): Observable<any> {
    return this._request.http(parametro);
  }


}
