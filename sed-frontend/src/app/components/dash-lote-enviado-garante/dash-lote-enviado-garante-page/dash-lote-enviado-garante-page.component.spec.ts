import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashLoteEnviadoGarantePageComponent } from './dash-lote-enviado-garante-page.component';

describe('DashLoteEnviadoGarantePageComponent', () => {
  let component: DashLoteEnviadoGarantePageComponent;
  let fixture: ComponentFixture<DashLoteEnviadoGarantePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashLoteEnviadoGarantePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashLoteEnviadoGarantePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
