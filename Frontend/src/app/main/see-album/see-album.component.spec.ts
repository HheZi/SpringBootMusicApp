import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeeAlbumComponent } from './see-album.component';

describe('SeeAlbumComponent', () => {
  let component: SeeAlbumComponent;
  let fixture: ComponentFixture<SeeAlbumComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SeeAlbumComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeeAlbumComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
