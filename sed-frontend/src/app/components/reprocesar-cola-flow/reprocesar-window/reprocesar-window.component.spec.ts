import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReprocesarWindowComponent } from './reprocesar-window.component';

describe('ReprocesarWindowComponent', () => {
  let component: ReprocesarWindowComponent;
  let fixture: ComponentFixture<ReprocesarWindowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReprocesarWindowComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReprocesarWindowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
