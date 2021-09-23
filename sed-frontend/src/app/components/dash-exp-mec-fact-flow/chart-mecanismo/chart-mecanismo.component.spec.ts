import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartMecanismoComponent } from './chart-mecanismo.component';

describe('ChartMecanismoComponent', () => {
  let component: ChartMecanismoComponent;
  let fixture: ComponentFixture<ChartMecanismoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChartMecanismoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChartMecanismoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
