import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LoaderComponent } from './components/loader/loader.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { ContentLayoutComponent } from './components/layout/content-layout/content-layout.component';
import { FullLayoutComponent } from './components/layout/full-layout/full-layout.component';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ModalComponent } from './components/modal/modal.component';
import { BotoneMenuModule } from './components/boton-menu/botone-menu.module';
import { DemoMaterialModule } from '../material-modules';
import { NgxSpinnerModule } from 'ngx-spinner';
import { PermissionModule } from '../permission/permission.module';


@NgModule({
  declarations: [
    LoaderComponent,
    HeaderComponent,
    FooterComponent,
    ContentLayoutComponent,
    FullLayoutComponent,
    ModalComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    FlexLayoutModule,
    BotoneMenuModule,
    DemoMaterialModule,
    NgxSpinnerModule,
    PermissionModule,

  ],
  exports: [
    LoaderComponent,
    ModalComponent,
  ],
  entryComponents: [
    ModalComponent,
  ],
  providers: [
    // NavService,
    DatePipe,
  ]
})
export class SharedModule { }

