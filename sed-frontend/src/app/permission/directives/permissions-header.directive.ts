import { Directive, Input, OnDestroy, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { Subject } from 'rxjs';
import { Permission } from '../models/permissions.enum';
import { PermissionService } from '../services/permission.service';
@Directive({
  selector: '[appPermissionsHeader]',
})
export class PermissionsHeaderDirective implements OnInit, OnDestroy {

  @Input() appPermissionsHeader: Permission[];

  private onDestroy$ = new Subject<boolean>();

  constructor(
    private permissionService: PermissionService,
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef
  ) {
  }

  ngOnInit() {
    this.getPermisions();
  }

  getPermisions() {
    if (this.permissionService.checkPermissionHeader(this.appPermissionsHeader)) {
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
