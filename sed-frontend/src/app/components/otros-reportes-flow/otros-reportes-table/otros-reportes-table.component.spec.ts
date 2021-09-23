import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtrosReportesTableComponent } from './otros-reportes-table.component';

describe('TableComponent', () => {
  let component: OtrosReportesTableComponent;
  let fixture: ComponentFixture<OtrosReportesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OtrosReportesTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OtrosReportesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
