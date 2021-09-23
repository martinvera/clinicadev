import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RolPageComponent } from './rol-page.component';

describe('RolPageComponent', () => {
  let component: RolPageComponent;
  let fixture: ComponentFixture<RolPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RolPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RolPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
