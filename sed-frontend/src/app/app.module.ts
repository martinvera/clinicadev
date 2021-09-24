import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from './shared/shared.module';
import { HttpClientModule } from '@angular/common/http';
import { LoginComponent } from './auth/login/login.component';
import { DemoMaterialModule } from './material-modules';
import { RecaptchaFormsModule, RecaptchaModule, RecaptchaSettings, RECAPTCHA_SETTINGS } from 'ng-recaptcha';
import { httpInterceptorProviders } from './shared/services/interceptor';
import { ToastrModule } from 'ngx-toastr';
import { ModalForgotComponent } from './auth/modal-forgot/modal-forgot.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ModalForgotComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    SharedModule,
    AppRoutingModule,
    HttpClientModule,
    DemoMaterialModule,
    RecaptchaModule,
    RecaptchaFormsModule,
  ],
  providers: [
    httpInterceptorProviders,
    {
      provide: RECAPTCHA_SETTINGS,
      useValue: {
        // siteKey: environment.llaveSecreta,
      } as RecaptchaSettings,
    }
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent]
})
export class AppModule { }
