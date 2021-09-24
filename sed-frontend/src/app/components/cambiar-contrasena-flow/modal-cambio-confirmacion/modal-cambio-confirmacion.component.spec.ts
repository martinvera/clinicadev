import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalCambioConfirmacionComponent } from './modal-cambio-confirmacion.component';

describe('ModalCambioConfirmacionComponent', () => {
  let component: ModalCambioConfirmacionComponent;
  let fixture: ComponentFixture<ModalCambioConfirmacionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalCambioConfirmacionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalCambioConfirmacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
