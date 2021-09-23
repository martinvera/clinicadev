import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LotesEnviadosGarantePageComponent } from './lotes-enviados-garante-page.component';

describe('LotesEnviadosGarantePageComponent', () => {
  let component: LotesEnviadosGarantePageComponent;
  let fixture: ComponentFixture<LotesEnviadosGarantePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LotesEnviadosGarantePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LotesEnviadosGarantePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
