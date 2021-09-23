import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ModalComponent } from 'src/app/shared/components/modal/modal.component';
import { NavService } from 'src/app/shared/services/nav.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss']
})
export class HomePageComponent  {
  exp_dig: boolean = false;
  ges_doc: boolean = false;
  box: boolean = true;
  constructor(
    public hppr: NavService,
    public dialog: MatDialog
  ) { }

  openDialog(): void {
    const dialogRef = this.dialog.open(ModalComponent, {
      width: '400px',
      panelClass: 'dialog-content-example',
    });
    dialogRef.afterClosed().subscribe();
  }

  

  back() {
    this.box = true;
    this.exp_dig = false;
  this.ges_doc = false;
  }

  asignar(e: any) {
    this.box = false
    if (e.view == 'ges_doc') {
      this.ges_doc = true;
    } else if (e.view == 'exp_dig') {
      this.exp_dig = true;
    }
  }
  toGestion() {
    this.box = false;
  }

}
