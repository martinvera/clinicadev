import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LotesEnviadosTableComponent } from './lotes-enviados-table.component';

describe('LotesEnviadosTableComponent', () => {
  let component: LotesEnviadosTableComponent;
  let fixture: ComponentFixture<LotesEnviadosTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LotesEnviadosTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LotesEnviadosTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
