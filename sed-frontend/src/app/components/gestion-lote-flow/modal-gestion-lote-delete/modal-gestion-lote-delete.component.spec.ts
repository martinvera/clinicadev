import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalGestionLoteDeleteComponent } from './modal-gestion-lote-delete.component';

describe('ModalGestionLoteDeleteComponent', () => {
  let component: ModalGestionLoteDeleteComponent;
  let fixture: ComponentFixture<ModalGestionLoteDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalGestionLoteDeleteComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalGestionLoteDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
