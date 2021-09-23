import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalReporteComponent } from './modal-reporte.component';

describe('ModalReporteComponent', () => {
  let component: ModalReporteComponent;
  let fixture: ComponentFixture<ModalReporteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalReporteComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalReporteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
