import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
} from '@angular/common/http';

import { Observable } from 'rxjs';
import { AuthService } from '../security/auth.service';
import { map } from 'rxjs/operators';
@Injectable()
export class httpHeaders implements HttpInterceptor {
  constructor(private _auth: AuthService) { }
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const token = this._auth.getToken();
    let posicion = request.url.indexOf('oauth/token');
    if (token && posicion == -1) {
      request = request.clone({ headers: request.headers.set('Authorization', 'Bearer ' + token) });
    }

    return next.handle(request).pipe(
      map((event: HttpEvent<any>) => {
        
        return event;
      })
    );
  }
}
