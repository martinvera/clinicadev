import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorModalParcialComponent } from './error-modal-parcial.component';

describe('ErrorModalParcialComponent', () => {
  let component: ErrorModalParcialComponent;
  let fixture: ComponentFixture<ErrorModalParcialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ErrorModalParcialComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorModalParcialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
