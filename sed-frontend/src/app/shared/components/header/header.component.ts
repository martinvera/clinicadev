
import { Component, OnInit } from '@angular/core';
import { Permission } from 'src/app/permission/models/permissions.enum';
import { NavService } from '../../services/nav.service';
import { AuthService } from '../../services/security/auth.service';

var body = document.getElementsByTagName("body")[0];

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  userName: any;
  permission = Permission;
  constructor(public navServices: NavService,
    private _authService: AuthService) {

  }

  ngOnInit() {
    this.userName = this._authService.getUser();
  }
  SignOut() {
    this._authService.SignOut();
  }

}
