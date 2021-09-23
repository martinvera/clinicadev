import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalForgotComponent } from './modal-forgot.component';

describe('ModalForgotComponent', () => {
  let component: ModalForgotComponent;
  let fixture: ComponentFixture<ModalForgotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalForgotComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalForgotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
