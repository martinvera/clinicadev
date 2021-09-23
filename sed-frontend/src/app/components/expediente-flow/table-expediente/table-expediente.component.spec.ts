import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableExpedienteComponent } from './table-expediente.component';

describe('TableExpedienteComponent', () => {
  let component: TableExpedienteComponent;
  let fixture: ComponentFixture<TableExpedienteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TableExpedienteComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableExpedienteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
