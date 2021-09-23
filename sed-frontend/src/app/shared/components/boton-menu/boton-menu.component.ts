import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-boton-menu',
  templateUrl: './boton-menu.component.html',
  styleUrls: ['./boton-menu.component.scss']
})
export class BotonMenuComponent implements OnInit {
  @Input() path: string | undefined;
  @Input() icon: string | undefined;
  @Input() text: string | undefined;
  @Input() clase: string | undefined;
  @Input() link: any;
  @Input() active: boolean | undefined;
  gestion: string | undefined;
  @Output() propagar = new EventEmitter();
  @Output() bread = new EventEmitter();
  @Input() permissions: any;
  @Input() permissionsMenu: any = []
  permissionsMenuLink: any = []
  permissionsLink: any;
  constructor(public route: Router) { }

  ngOnInit() {
    this.permissionsLink = parseInt(this.permissions);
    if (this.permissionsMenu.length >0) {
      this.permissionsMenuLink = this.permissionsMenu?.split(',');
    }
  }

  toGestion(path: any) {
    if (path === '') {
      this.propagar.emit(this.gestion)
    } else {
      this.route.navigate([path]);
    }
  }

}
