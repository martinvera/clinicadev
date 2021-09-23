import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BotonMenuComponent } from './boton-menu.component';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { PermissionModule } from 'src/app/permission/permission.module';


@NgModule({
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  declarations: [BotonMenuComponent],
  imports: [
    CommonModule,
    MatIconModule,
    RouterModule,
    PermissionModule
  ],
  exports: [BotonMenuComponent]
})
export class BotoneMenuModule { }