import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReprocesarColaPageComponent } from './reprocesar-cola-page.component';

describe('ReprocesarColaPageComponent', () => {
  let component: ReprocesarColaPageComponent;
  let fixture: ComponentFixture<ReprocesarColaPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReprocesarColaPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReprocesarColaPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
