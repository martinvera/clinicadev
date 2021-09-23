import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { tap } from 'rxjs/operators';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private _router: Router,
    private toastrService: ToastrService,
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request)
      .pipe(
        tap(
          event => {
            if (event instanceof HttpResponse) {
              console.log("evento")
            }
          },
          error => {
            if (error.status == 0) {
              this.toastrService.error('Error desconocido', 'ERROR!');
            } else if (error.status == 304) {
              this.toastrService.error('Error 304', 'ERROR!');

            } else if (error.status == 400) {
              this.toastrService.error('Petición Incorrecta' + error.status, 'ERROR 400');

            } else if (error.status == 401) {

              this.toastrService.warning('Se ha rechazado la autorización sus credenciales. ', 'ERROR 401');
              this._router.navigateByUrl('/login');
            } else if (error.status == 403) {
              this.toastrService.error(error.message);
              this._router.navigateByUrl('/error/403');
            } else if (error.status == 404) {
              this.toastrService.error(error.message);
              this._router.navigateByUrl('/error/404');
            } else if (error.status == 500) {
              this.toastrService.error(error.message);

            } else if (error.status == 503) {
              this.toastrService.error(error.message);
              this._router.navigateByUrl('/error/503');
            } else if (error.status >= 900 && error.status < 1000) {
              this.toastrService.error('Error en servicio' + error.status, 'ERROR');
            } else {
              this.toastrService.error('Error en servicio' + error.status, 'ERROR');
            }
          })
      )
  }
}
