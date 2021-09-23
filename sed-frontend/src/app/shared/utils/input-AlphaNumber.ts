import { Injectable } from "@angular/core";
@Injectable({
  providedIn: 'root'
})

export class InputAlphaNumber {
  validAlphaNumber(e: any) {
    
    if (!e.key.match(/[a-zA-Z0-9]/)) {
      e.preventDefault();
    }
  }
}