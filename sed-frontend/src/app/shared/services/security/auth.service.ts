import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Parameter } from '../../model/common/parameter';
import { ENDPOINT_USERS } from '../../utils/apis';
import { UsersService } from '../clinica-internacional/users.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  dellSuscribe: Subscription = new Subscription();
  public showLoader: boolean = false;
  public userData: any;

  time: number;
  constructor(
    private router: Router,
    private userService: UsersService
  ) {
    this.time = 1600;
  }
  userName(user: any) {
    if (localStorage.getItem('user')) {
      localStorage.setItem('user', user);

    } else {
      localStorage.setItem('user', '');
      localStorage.setItem('user', user);
    }
    this.getRollUser(user);
    setTimeout(() => {
      this.getRedirect();
    }, this.time);
  }
  AuthLoginToken(token: any) {
    if (localStorage.getItem('access_token')) {
      localStorage.setItem('access_token', token);

    } else {
      localStorage.setItem('access_token', '');
      localStorage.setItem('access_token', token);
    }
    setTimeout(() => {
      this.getRedirect();
    }, this.time);
  }
  getRedirect() {

   let path= localStorage.getItem("access_token") &&
      window.location.pathname === "/login"
      ? this.router.navigate(["/"])
      : null;
      return path;
  }

  getToken() {
    return localStorage.getItem('access_token') ? localStorage.getItem('access_token') : '';
  }
  getUser() {
    return localStorage.getItem('user') ? localStorage.getItem('user') : '';
  }
  // // Sign out
  SignOut() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
  async getRollUser(user: any) {
    const parametro: Parameter = new Parameter();
    parametro.url = ENDPOINT_USERS.GET_ROL_USER.replace(':user', user);
    parametro.request = 'GET';

    this.dellSuscribe.add(this.userService.getRolUser(parametro).pipe(
    ).subscribe(
      (data: any) => {
        this.setRolUser(data.response.data.rol.privilegioList);
      }
    ));
  }
  async setRolUser(rol: any) {
    if (localStorage.getItem('rol')) {
      localStorage.setItem('rol', JSON.stringify(rol));
    } else {
      localStorage.setItem('rol', '');
      localStorage.setItem('rol', JSON.stringify(rol));
    }
  }
  getRolUserStorage() {
    return JSON.parse(localStorage.getItem('rol')) ? JSON.parse(localStorage.getItem('rol')) : null;
  }

}



