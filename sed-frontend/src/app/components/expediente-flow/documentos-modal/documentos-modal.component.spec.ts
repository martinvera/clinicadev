import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentosModalComponent } from './documentos-modal.component';

describe('DocumentosModalComponent', () => {
  let component: DocumentosModalComponent;
  let fixture: ComponentFixture<DocumentosModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentosModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentosModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
