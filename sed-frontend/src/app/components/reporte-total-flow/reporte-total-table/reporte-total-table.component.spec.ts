import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporteTotalTableComponent } from './reporte-total-table.component';

describe('TableComponent', () => {
  let component: ReporteTotalTableComponent;
  let fixture: ComponentFixture<ReporteTotalTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReporteTotalTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReporteTotalTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
