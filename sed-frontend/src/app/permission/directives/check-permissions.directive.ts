import { Directive, Input, OnDestroy, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { Subject } from 'rxjs';
import { Permission } from '../models/permissions.enum';
import { PermissionService } from '../services/permission.service';
@Directive({
  selector: '[appCheckPermissions]',
})
export class CheckPermissionsDirective implements OnInit, OnDestroy {

  @Input() appCheckPermissions: Permission;


  private onDestroy$ = new Subject<boolean>();

  constructor(
    private permissionService: PermissionService,
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef
  ) {
  }

  ngOnInit() {
    if (this.permissionService.checkPermission(this.appCheckPermissions)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }

  ngOnDestroy() {
    this.onDestroy$.next(true);
    this.onDestroy$.unsubscribe();
  }
}
