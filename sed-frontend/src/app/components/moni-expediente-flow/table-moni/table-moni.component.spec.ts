import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableMoniComponent } from './table-moni.component';

describe('TableMoniComponent', () => {
  let component: TableMoniComponent;
  let fixture: ComponentFixture<TableMoniComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TableMoniComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableMoniComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
