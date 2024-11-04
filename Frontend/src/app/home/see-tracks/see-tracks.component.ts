import { Component, OnInit, ViewChild } from '@angular/core';
import { TrackService } from '../../services/track/track.service';
import { MessageService } from 'primeng/api';
import { Track } from './track';
import { Title } from '@angular/platform-browser';
import { AuthorService } from '../../services/author/author.service';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { ActivatedRoute } from '@angular/router';
import { HttpParams } from '@angular/common/http';
import { AudioService } from '../../services/audio/audio.service';

@Component({
  selector: 'app-see-tracks',
  templateUrl: './see-tracks.component.html',
  styleUrls: ['./see-tracks.component.css'],
})
export class SeeTracksComponent implements OnInit {


  public isNotFound: boolean = false;
  public tracks: Track[] = [];
  public radioVal: string = "Track";
  public searchValue!: string;

  constructor(
    private trackService: TrackService,
    private messageService: MessageService,
    private titleService: Title,
    private authorService: AuthorService,
    private playlistService: PlaylistService,
    private activatedRoute: ActivatedRoute,
    private audioService: AudioService
  ) {
    this.titleService.setTitle("Tracks");
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.searchValue = params['name'] || null;
      if (this.searchValue) {
        this.getTracksByName();
      }
    });
    if (!this.searchValue) {
      this.getTracks();
    }
  }

  public getTracks(headers: HttpParams | null = null): void {
    this.trackService.getTracks(headers).subscribe({
      next: tracksResp => this.populateTracks(tracksResp),
      error: err => this.messageService.add({
        closable: true,
        summary: "Error while loading tracks",
        detail: err.error,
        severity: "error"
      })
    });
  }

  private populateTracks(tracksResp: any): void {
    this.tracks = [];
    this.checkIfTracksNotFound(tracksResp);
    if (!this.isNotFound)
      tracksResp.forEach((track: any) => {
        this.authorService.getAuthorsById(track.authorId).subscribe({
          next: (author: any) => {
            this.playlistService.getPlaylists(track.playlistId).subscribe({
              next: (playlist: any) => {
                this.tracks.push({
                  title: track.title,
                  audioUrl: track.audioUrl,
                  author: author.name,
                  imageUrl: playlist.imageUrl,
                  playlist: playlist.name,
                  isNowPlaying: false
                });
              }
            });
          }
        });
      });
  }

  public getTracksByName(): void {
    console.log(this.radioVal);

    if (this.radioVal === "Track") {
      var params = new HttpParams();
      this.getTracks(params.append("name", this.searchValue))
    } else if (this.radioVal === "Author") {
      this.authorService.getAuthorsBySymbol(this.searchValue).subscribe({
        next: (authors: any) => this.fetchTracksByAuthors(authors),
        error: err => this.handleError("Error while loading authors", err)
      });
    } else if (this.radioVal === "Playlist") {
      this.playlistService.getPlaylistsBySymbol(this.searchValue).subscribe({
        next: (playlists: any) => this.fetchTracksByPlaylists(playlists),
        error: err => this.handleError("Error while loading playlists", err)
      });
    }
  }

  private checkIfTracksNotFound(value: any): void {
    this.isNotFound = (value as Array<Object>).length > 0 ? false : true;
  }

  private fetchTracksByAuthors(authors: any[]): void {
    this.checkIfTracksNotFound(authors)
    if (!this.isNotFound)
      authors.forEach((author: any) => {
        this.trackService.getTracksByAuthorId(author.id).subscribe({
          next: (tracks: any) => this.populateTracks(tracks),
          error: (err: any) => this.handleError("Error while loading tracks by author", err)
        });
      });
  }

  private fetchTracksByPlaylists(playlists: any[]): void {
    this.checkIfTracksNotFound(playlists)
    if (!this.isNotFound)
      playlists.forEach(playlist => {
        this.trackService.getTracksByPlaylistId(playlist.id).subscribe({
          next: (tracks: any) => this.populateTracks(tracks),
          error: (err: any) => this.handleError("Error while loading tracks by playlist", err)
        });
      });
  }

  public playTrack(index: number): void {
    this.audioService.setTracks(this.tracks, index);
  }

  private handleError(summary: string, error: any): void {
    this.messageService.add({ closable: true, summary, detail: error.error, severity: "error" });
  }

}
