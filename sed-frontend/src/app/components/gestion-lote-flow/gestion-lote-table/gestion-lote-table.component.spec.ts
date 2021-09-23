import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionLoteTableComponent } from './gestion-lote-table.component';

describe('GestionLoteTableComponent', () => {
  let component: GestionLoteTableComponent;
  let fixture: ComponentFixture<GestionLoteTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GestionLoteTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GestionLoteTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
