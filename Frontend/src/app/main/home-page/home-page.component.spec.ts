import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeeTracksComponent } from './home-page.component';

describe('SeeTracksComponent', () => {
  let component: SeeTracksComponent;
  let fixture: ComponentFixture<SeeTracksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SeeTracksComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeeTracksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
