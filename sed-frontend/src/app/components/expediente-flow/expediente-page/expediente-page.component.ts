import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { CatalogoService } from 'src/app/shared/services/clinica-internacional/catalogo.service';

@Component({
  selector: 'app-expediente-page',
  templateUrl: './expediente-page.component.html',
  styleUrls: ['./expediente-page.component.scss']
})
export class ExpedientePageComponent implements OnInit {
  data: any;
  numLoteGeneral: number;
  constructor(
    public dialog: MatDialog,
    private catalogoService: CatalogoService,
    private router: Router,
  ) { }


 

  
  ngOnInit(): void {
    if (this.catalogoService.transportLote() > 0) {
      this.numLoteGeneral = this.catalogoService.transportLote();
    } else {
      this.router.navigate(['/gestion/lote']);
    }

  }
}
