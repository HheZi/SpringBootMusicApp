import { AfterViewInit, Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { TrackService } from '../../services/track/track.service';
import { MessageService } from 'primeng/api';
import { Title } from '@angular/platform-browser';
import { AuthorService } from '../../services/author/author.service';
import { AlbumService } from '../../services/album/album.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpParams } from '@angular/common/http';
import { PlaylistListComponent } from '../playlist-list/playlist-list.component';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { TrackListComponent } from '../track-list/track-list.component';
import { NotFoundError } from 'rxjs';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css'],
})
export class HomeComponent implements OnInit {

  @ViewChild(PlaylistListComponent) playlistList!: PlaylistListComponent; 
  @ViewChild(TrackListComponent) trackList!: TrackListComponent;

  public isNotFound: boolean = false;
  public radioVal: string = "Track";
  public searchValue: string = '';

  constructor(
    private trackService: TrackService,
    private messageService: MessageService,
    private titleService: Title,
    private authorService: AuthorService,
    private albumService: AlbumService,
    private activatedRoute: ActivatedRoute,
    private playlistService: PlaylistService,
  ) {
    this.titleService.setTitle("Tracks");
  }
  
  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.searchValue = params['name'] || null;
      if (this.searchValue) {
        this.getTracksByName();
      }
      else{
        this.getTracks();
      }
    });
  }


  
  public getTracks(headers: HttpParams | null = null): void {
    this.isNotFound = false;
    this.playlistList?.setPlaylists([]);
    this.trackList?.setTracks(null);
    
    this.trackService.getTracks(headers).subscribe({
      next: tracksResp => this.populateTracks(tracksResp),
      error: () => this.isNotFound = true
    });
  }
  
  private populateTracks(tracksResp: any): void {
    this.checkIfNotFound(tracksResp);
    
    if (!this.isNotFound) {
      this.trackList.setTracks(tracksResp);
    }
  }
  
  public getTracksByName(): void {
    this.isNotFound = false;
    this.playlistList?.setPlaylists([]);
    this.trackList?.setTracks(null);

    if (this.radioVal === "Track") {
      var params = new HttpParams();
      this.getTracks(params.append("name", this.searchValue))
    } else if (this.radioVal === "Author") {
      this.authorService.getAuthorsBySymbol(this.searchValue).subscribe({
        next: (authors: any) => this.fetchTracksByAuthors(authors),
        error: err => this.isNotFound = true
      });
    } else if (this.radioVal === "Album") {
      this.albumService.getAlbumsBySymbol(this.searchValue).subscribe({
        next: (albums: any) => this.fetchTracksByAlbums(albums),
        error: err => this.isNotFound = true
      });
    } else if(this.radioVal === "Playlist"){
      this.playlistService.getTracksBySymbol(this.searchValue).subscribe({
        next: (playlists: any) => this.fetchPlaylist(playlists),
        error: err => this.isNotFound = true
      })
    }
  }

  private checkIfNotFound(value: any): void {
    this.isNotFound = value.empty;
  }

  private fetchTracksByAuthors(authors: any[]): void {
    this.checkIfNotFound(authors)
    if (!this.isNotFound){
      var ids = authors.map(a => a.id);
      this.albumService.getAlbumsByAuthorId(ids).subscribe({
        next: (albums: any) => {
          this.trackService.getTracksByAlbumId(albums.map((a: any) => a.id)).subscribe({
            next: (tracks: any) => this.populateTracks(tracks),
            error: () => this.isNotFound = true 
          })
        },
        error: () => this.isNotFound = true
      })
    }
  }

  private fetchTracksByAlbums(albums: any[]): void {
    this.checkIfNotFound(albums)
    if (!this.isNotFound){
      var ids = albums.map((a: any) => a.id);
      this.trackService.getTracksByAlbumId(ids).subscribe({
        next: (tracks: any) => this.populateTracks(tracks),
        error: (err: any) => this.handleError("Error while loading tracks by album", err)
      });
    }
  }

  private fetchPlaylist(playlists: any[]): void {
    this.checkIfNotFound(playlists);
  
    if (!this.isNotFound) {
      this.playlistList.setPlaylists(playlists); 
    }
  }

  private handleError(summary: string, error: any): void {
    this.messageService.add({ closable: true, summary, detail: error.error, severity: "error" });
  }

}
