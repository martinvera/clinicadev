import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PermissionService } from './services/permission.service';
import { CheckPermissionsDirective } from './directives/check-permissions.directive';
import { PermissionsHeaderDirective } from './directives/permissions-header.directive';

@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    CheckPermissionsDirective,
    PermissionsHeaderDirective
  ],
  exports: [
    CheckPermissionsDirective,
    PermissionsHeaderDirective
  ],
  providers: [
    PermissionService,
  ],
})
export class PermissionModule {
}
