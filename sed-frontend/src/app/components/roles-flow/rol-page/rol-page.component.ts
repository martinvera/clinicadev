import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CrearRolComponent } from '../crear-rol/crear-rol.component';

@Component({
  selector: 'app-rol-page',
  templateUrl: './rol-page.component.html',
  styleUrls: ['./rol-page.component.scss']
})
export class RolPageComponent  {



  constructor(public dialog: MatDialog) { }
  openDialog(): void {
    const dialogRef = this.dialog.open(CrearRolComponent, {

    });
    dialogRef.afterClosed().subscribe();
  }

 
}
