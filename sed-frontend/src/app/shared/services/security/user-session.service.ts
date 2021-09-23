import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserSessionService {

  

  setToken(data: string): void {
    localStorage.setItem('token', data);
  }

  removeToken(): void {
    localStorage.removeItem('token');
  }
}
