import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporteTotalPageComponent } from './reporte-total-page.component';

describe('ReporteTotalPageComponent', () => {
  let component: ReporteTotalPageComponent;
  let fixture: ComponentFixture<ReporteTotalPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReporteTotalPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReporteTotalPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
