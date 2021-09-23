import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LotesEnviadosModalComponent } from './lotes-enviados-modal.component';

describe('LotesEnviadosModalComponent', () => {
  let component: LotesEnviadosModalComponent;
  let fixture: ComponentFixture<LotesEnviadosModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LotesEnviadosModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LotesEnviadosModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
