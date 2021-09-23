import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LotesEnviadosLogComponent } from './lotes-enviados-log.component';

describe('LotesEnviadosLogComponent', () => {
  let component: LotesEnviadosLogComponent;
  let fixture: ComponentFixture<LotesEnviadosLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LotesEnviadosLogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LotesEnviadosLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
