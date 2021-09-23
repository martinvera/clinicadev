import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoniExpedientePageComponent } from './moni-expediente-page.component';

describe('MoniExpedientePageComponent', () => {
  let component: MoniExpedientePageComponent;
  let fixture: ComponentFixture<MoniExpedientePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MoniExpedientePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MoniExpedientePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
