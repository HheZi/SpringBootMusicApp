import { Component, OnInit } from '@angular/core';
import { TrackService } from '../../services/track/track.service';
import { MessageService } from 'primeng/api';
import { Track } from './track';
import { AudioComponent } from '../audio/audio.component';
import { Title } from '@angular/platform-browser';
import { AuthorService } from '../../services/author/author.service';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { ActivatedRoute } from '@angular/router';
import { HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-see-tracks',
  templateUrl: './see-tracks.component.html',
  styleUrl: './see-tracks.component.css',
})
export class SeeTracksComponent implements OnInit {

  public tracks: Track[] = [];
  public radioVal?: string;
  public searchValue: string | null = null;

  constructor(private trackService: TrackService, private messageService: MessageService, private title: Title,
    private authorService: AuthorService, private playlistService: PlaylistService, private activeRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.activeRoute.queryParams.subscribe((params: any) => {
      this.searchValue = params['name'];
    });
    this.title.setTitle("Tracks")
    this.getTracks(null);
  }

  private getTracks(header: any): void{
    this.tracks = [];
    this.trackService.getTracks(header).subscribe({
      error: err => this.messageService.add({ closable: true, summary: "Error while loading a tracks", detail: err.error, severity: "error" }),

      next: (tracksResp: any) => {
        tracksResp.forEach((track: any) => {
          this.authorService.getAuthorsById(track.authorId).subscribe({
            next: (author: any) => {
              this.playlistService.getPlaylists(track.playlistId).subscribe({
                next: (playlist: any) => {
                  this.tracks.push({title: track.title, audioUrl: track.audioUrl, author: 
                    author.name, imageUrl: playlist.imageUrl, playlist: playlist.name})
                }
              })
            }
          })

        });
      }
    });
  }

  public getTracksByName(): void{
    if(this.radioVal === "Track"){
      this.getTracks({name: this.searchValue})
    }
    else if(this.radioVal === "Author"){
      this.authorService.getAuthorsBySymbol(this.searchValue as string).subscribe((resp: any) => {
        var header = new HttpHeaders()
        header.set("authorId", resp.join(","))
        console.log(header);
      });
    }
    else if(this.radioVal === "Playlist"){
      this.playlistService.getPlaylistsBySymbol(this.searchValue as string).subscribe(() => this.getTracks(null));
    }
  }

  public playTrack(track: Track): void {
    AudioComponent.playAudio(track);
  }

}