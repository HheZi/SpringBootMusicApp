import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeeAuthorComponent } from './see-author.component';

describe('SeeAuthorComponent', () => {
  let component: SeeAuthorComponent;
  let fixture: ComponentFixture<SeeAuthorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SeeAuthorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeeAuthorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
