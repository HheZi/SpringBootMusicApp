import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeePlaylistComponent } from './see-playlist.component';

describe('SeePlaylistComponent', () => {
  let component: SeePlaylistComponent;
  let fixture: ComponentFixture<SeePlaylistComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SeePlaylistComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeePlaylistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
