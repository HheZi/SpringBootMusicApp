import { Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { TrackService } from '../../services/track/track.service';
import { MessageService } from 'primeng/api';
import { Track } from './track';
import { Title } from '@angular/platform-browser';
import { AuthorService } from '../../services/author/author.service';
import { AlbumService } from '../../services/album/album.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpParams } from '@angular/common/http';
import { AudioService } from '../../services/audio/audio.service';
import { PlaylistListComponent } from '../playlist-list/playlist-list.component';
import { PlaylistService } from '../../services/playlist/playlist.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css'],
})
export class SeeTracksComponent implements OnInit {

  @ViewChild(PlaylistListComponent, {static: false}) playlistList!: PlaylistListComponent; 

  public isNotFound: boolean = false;
  public tracks: Track[] = [];
  public radioVal: string = "Track";
  public searchValue: string = '';

  constructor(
    private trackService: TrackService,
    private messageService: MessageService,
    private titleService: Title,
    private authorService: AuthorService,
    private albumService: AlbumService,
    private activatedRoute: ActivatedRoute,
    private audioService: AudioService,
    private playlistService: PlaylistService,
    private router: Router
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
    this.trackService.getTracks(headers).subscribe({
      next: tracksResp => this.populateTracks(tracksResp)
    });
  }

  private populateTracks(tracksResp: any): void {
    this.tracks = [];
    this.checkIfNotFound(tracksResp);
  
    if (!this.isNotFound) {
      tracksResp.forEach((track: any) => {
        this.tracks.push({
          id: track.id,
          title: track.title,
          audioUrl: track.audioUrl,
          author: track.author.name,  
          authorId: track.author.id,
          imageUrl: track.album.imageUrl,  
          albumName: track.album.name,  
          albumId: track.album.id,
          isNowPlaying: false,
          duration: track.duration,
          isEditing: false
        });
      });
    }
  }

  public getTracksByName(): void {
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
    this.isNotFound = (value as Array<Object>).length > 0 ? false : true;
  }

  private fetchTracksByAuthors(authors: any[]): void {
    this.checkIfNotFound(authors)
    if (!this.isNotFound)
      authors.forEach((author: any) => {
        this.trackService.getTracksByAuthorId(author.id).subscribe({
          next: (tracks: any) => this.populateTracks(tracks),
          error: (err: any) => this.handleError("Error while loading tracks by author", err)
        });
      });
  }

  private fetchTracksByAlbums(albums: any[]): void {
    this.checkIfNotFound(albums)
    if (!this.isNotFound)
      albums.forEach(album => {
        this.trackService.getTracksByAlbumId(album.id).subscribe({
          next: (tracks: any) => this.populateTracks(tracks),
          error: (err: any) => this.handleError("Error while loading tracks by album", err)
        });
      });
  }

  private fetchPlaylist(playlists: any[]): void{
    this.checkIfNotFound(playlists);
    if(!this.isNotFound){
      this.tracks = [];
      this.playlistList.setPlaylists(playlists);
    }

  }

  public playTrack(index: number): void {
    this.audioService.setTracks(this.tracks, index);
  }

  public seeAlbum(value: number): void{
    this.router.navigate(["/album/"+ value]);
  }

  public seeAuthor(value: number){
    this.router.navigate(["author/" + value]);
  }

  private handleError(summary: string, error: any): void {
    this.messageService.add({ closable: true, summary, detail: error.error, severity: "error" });
  }

}
