import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { FavoriteService } from '../../services/favorite/favorite.service';
import { Title } from '@angular/platform-browser';
import { TrackListComponent } from '../track-list/track-list.component';
import { TrackService } from '../../services/track/track.service';

@Component({
  selector: 'app-see-favorite-tracks',
  templateUrl: './see-favorite-tracks.component.html',
  styleUrl: './see-favorite-tracks.component.css'
})
export class SeeFavoriteTracksComponent implements AfterViewInit{

  @ViewChild(TrackListComponent)
  private trackList!: TrackListComponent;
  private favoriteTracksId: number[] = [];

  constructor(
    private favoriteService: FavoriteService,
    private trackService: TrackService,
    private title: Title
  ){}

  ngAfterViewInit(): void {
    this.title.setTitle("Favorite Tracks");
    this.favoriteService.getFavoritesTracks().subscribe({
      next: (resp: any) => {
        this.favoriteTracksId = resp;
        if(this.favoriteTracksId.length !== 0){
          this.trackList.setTracks((page:number) => this.trackService.getTrackByIds(this.favoriteTracksId, page));
        }
        else{
          this.trackList.setTracksNotFound(true);
        }
      }
    })
  }

}
