import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalMensajeErrorComponent } from './modal-mensaje-error.component';

describe('ModalMensajeErrorComponent', () => {
  let component: ModalMensajeErrorComponent;
  let fixture: ComponentFixture<ModalMensajeErrorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalMensajeErrorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalMensajeErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
