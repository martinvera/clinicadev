import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewRolComponent } from './view-rol.component';

describe('ViewRolComponent', () => {
  let component: ViewRolComponent;
  let fixture: ComponentFixture<ViewRolComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewRolComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewRolComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
