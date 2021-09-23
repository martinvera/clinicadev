import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CargadocPageComponent } from './cargadoc-page.component';

describe('CargadocPageComponent', () => {
  let component: CargadocPageComponent;
  let fixture: ComponentFixture<CargadocPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CargadocPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CargadocPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
