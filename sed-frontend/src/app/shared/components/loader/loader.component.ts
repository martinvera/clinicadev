import { Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { LoaderService } from '../../services/common/loader.service';

@Component({
  selector: 'app-loader',
  templateUrl: './loader.component.html',
})
export class LoaderComponent implements OnInit {

  public show: boolean = true;

  constructor(
    private spinner: NgxSpinnerService,
    private _loaderService: LoaderService
  ) {
  }

  ngOnInit() {
    this._loaderService.loaderState.subscribe(
      value => {
        value.show ? this.spinner.show() : this.spinner.hide();
      });
  }

  

}
