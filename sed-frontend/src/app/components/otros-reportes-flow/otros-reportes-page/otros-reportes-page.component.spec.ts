import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtrosReportesPageComponent } from './otros-reportes-page.component';

describe('OtrosReportesPageComponent', () => {
  let component: OtrosReportesPageComponent;
  let fixture: ComponentFixture<OtrosReportesPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OtrosReportesPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OtrosReportesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
