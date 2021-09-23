import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporteParcialPageComponent } from './reporte-parcial-page.component';

describe('ReporteParcialPageComponent', () => {
  let component: ReporteParcialPageComponent;
  let fixture: ComponentFixture<ReporteParcialPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReporteParcialPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReporteParcialPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
