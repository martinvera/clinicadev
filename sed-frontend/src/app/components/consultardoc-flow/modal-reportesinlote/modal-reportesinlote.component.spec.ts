import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalReportesinloteComponent } from './modal-reportesinlote.component';

describe('ModalReportesinloteComponent', () => {
  let component: ModalReportesinloteComponent;
  let fixture: ComponentFixture<ModalReportesinloteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalReportesinloteComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalReportesinloteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
