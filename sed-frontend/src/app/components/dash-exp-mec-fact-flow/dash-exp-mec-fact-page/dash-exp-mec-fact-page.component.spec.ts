import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashExpMecFactPageComponent } from './dash-exp-mec-fact-page.component';

describe('DashExpMecFactPageComponent', () => {
  let component: DashExpMecFactPageComponent;
  let fixture: ComponentFixture<DashExpMecFactPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashExpMecFactPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashExpMecFactPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
