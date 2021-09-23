import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalReprocesoComponent } from './modal-reproceso.component';

describe('ModalReprocesoComponent', () => {
  let component: ModalReprocesoComponent;
  let fixture: ComponentFixture<ModalReprocesoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalReprocesoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalReprocesoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
