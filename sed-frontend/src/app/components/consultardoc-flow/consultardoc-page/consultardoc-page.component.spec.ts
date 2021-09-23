import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultardocPageComponent } from './consultardoc-page.component';

describe('ConsultardocPageComponent', () => {
  let component: ConsultardocPageComponent;
  let fixture: ComponentFixture<ConsultardocPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConsultardocPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsultardocPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
