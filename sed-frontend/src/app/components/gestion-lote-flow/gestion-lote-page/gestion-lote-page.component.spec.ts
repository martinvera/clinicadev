import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionLotePageComponent } from './gestion-lote-page.component';

describe('GestionLotePageComponent', () => {
  let component: GestionLotePageComponent;
  let fixture: ComponentFixture<GestionLotePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GestionLotePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GestionLotePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
