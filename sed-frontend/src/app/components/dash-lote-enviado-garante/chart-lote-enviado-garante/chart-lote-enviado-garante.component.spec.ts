import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartLoteEnviadoGaranteComponent } from './chart-lote-enviado-garante.component';

describe('ChartLoteEnviadoGaranteComponent', () => {
  let component: ChartLoteEnviadoGaranteComponent;
  let fixture: ComponentFixture<ChartLoteEnviadoGaranteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChartLoteEnviadoGaranteComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChartLoteEnviadoGaranteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
