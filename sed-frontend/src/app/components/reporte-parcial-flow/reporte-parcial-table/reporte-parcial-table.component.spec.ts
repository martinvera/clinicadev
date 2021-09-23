import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporteParcialTableComponent } from './reporte-parcial-table.component';

describe('TableComponent', () => {
  let component: ReporteParcialTableComponent;
  let fixture: ComponentFixture<ReporteParcialTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReporteParcialTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReporteParcialTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
