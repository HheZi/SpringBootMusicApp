import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeeFavoriteTracksComponent } from './see-favorite-tracks.component';

describe('SeeFavoriteTracksComponent', () => {
  let component: SeeFavoriteTracksComponent;
  let fixture: ComponentFixture<SeeFavoriteTracksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SeeFavoriteTracksComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeeFavoriteTracksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
