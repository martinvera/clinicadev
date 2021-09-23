import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorDocumentosModalComponent } from './error-documentos-modal.component';

describe('ErrorDocumentosModalComponent', () => {
  let component: ErrorDocumentosModalComponent;
  let fixture: ComponentFixture<ErrorDocumentosModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ErrorDocumentosModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorDocumentosModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
