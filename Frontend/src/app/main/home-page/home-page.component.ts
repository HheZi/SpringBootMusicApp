import { AfterContentInit, AfterViewInit, Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { TrackService } from '../../services/track/track.service';
import { MessageService } from 'primeng/api';
import { Title } from '@angular/platform-browser';
import { AuthorService } from '../../services/author/author.service';
import { AlbumService } from '../../services/album/album.service';
import { ActivatedRoute } from '@angular/router';
import { HttpParams } from '@angular/common/http';
import { PlaylistListComponent } from '../playlist-list/playlist-list.component';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { TrackListComponent } from '../track-list/track-list.component';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css'],
})
export class HomeComponent implements  AfterViewInit {

  @ViewChild(PlaylistListComponent) playlistList!: PlaylistListComponent; 
  @ViewChild(TrackListComponent) trackList!: TrackListComponent;

  public isNotFound: boolean = false;
  public radioVal: string = "Track";
  public searchValue: string = '';

  constructor(
    private trackService: TrackService,
    private titleService: Title,
    private authorService: AuthorService,
    private albumService: AlbumService,
    private activatedRoute: ActivatedRoute,
    private playlistService: PlaylistService,
  ) {
    this.titleService.setTitle("Tracks");
  }
  ngAfterViewInit(): void {
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

  public getTracks(): void {
    this.playlistList.makePlaylistEmpty();
    this.trackList.makeTracksEmpty();
    this.trackList.setTracks((page: number) => this.trackService.getTracks(new HttpParams().append("page", page)));
  }

  public getTracksByName(): void {
    this.playlistList.makePlaylistEmpty();
    this.trackList.makeTracksEmpty();
    
    if (this.radioVal === "Track") {
      this.fetchTracksByName();
    } else if (this.radioVal === "Author") {
      this.authorService.getAuthorsBySymbol(this.searchValue).subscribe({
        next: (authors: any) => this.fetchTracksByAuthors(authors),
        error: err => this.trackList.setTracksNotFound(true)
      });
    } else if (this.radioVal === "Album") {
      this.albumService.getAlbumsBySymbol(this.searchValue).subscribe({
        next: (albums: any) => this.fetchTracksByAlbums(albums),
        error: err => this.trackList.setTracksNotFound(true)
      });
    } else if(this.radioVal === "Playlist"){
      this.playlistService.getPlaylistBySymbol(this.searchValue).subscribe({
        next: (playlists: any) => this.fetchPlaylist(playlists),
        error: err => this.playlistList.notFound()
      })
    }
  }

  private fetchTracksByName(): void{
    this.trackList.setTracks((page: number) => this.trackService.getTracksByTrackName(this.searchValue, page));
  }

  private fetchTracksByAuthors(authors: any[]): void {
      var ids = authors.map(a => a.id);
      this.albumService.getAlbumsByAuthorId(ids).subscribe({
        next: (albums: any) => this.fetchTracksByAlbums(albums),
        error: () => this.isNotFound = true
      })
  }

  private fetchTracksByAlbums(albums: any[]): void {
      var ids = albums.map((a: any) => a.id);
      this.trackList.setTracks((page: number) => this.trackService.getTracksByAlbumId(ids, page));
  }

  private fetchPlaylist(playlists: any[]): void {
      this.playlistList.setPlaylists(playlists); 
  }

}
