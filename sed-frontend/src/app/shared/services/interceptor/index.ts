import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { httpHeaders } from './header.interceptor';
import { LoaderInterceptorService } from './loader.interceptor';
import { ErrorInterceptor } from './error.interceptor';


export const httpInterceptorProviders = [
    { provide: HTTP_INTERCEPTORS, useClass: httpHeaders, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: LoaderInterceptorService, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
];
