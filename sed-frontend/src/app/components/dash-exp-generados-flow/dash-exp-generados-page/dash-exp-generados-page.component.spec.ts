import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashExpGeneradosPageComponent } from './dash-exp-generados-page.component';

describe('DashExpGeneradosPageComponent', () => {
  let component: DashExpGeneradosPageComponent;
  let fixture: ComponentFixture<DashExpGeneradosPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashExpGeneradosPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashExpGeneradosPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
