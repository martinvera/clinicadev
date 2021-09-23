import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableDocsComponent } from './table-docs.component';

describe('TableDocsComponent', () => {
  let component: TableDocsComponent;
  let fixture: ComponentFixture<TableDocsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TableDocsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableDocsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
