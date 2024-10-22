import { Component, OnInit } from '@angular/core';
import { TrackService } from '../../services/track/track.service';
import { MessageService } from 'primeng/api';
import { Track } from './track';
import { AudioComponent } from '../audio/audio.component';
import { Title } from '@angular/platform-browser';
import { AuthorService } from '../../services/author/author.service';
import { PlaylistService } from '../../services/playlist/playlist.service';

@Component({
  selector: 'app-see-tracks',
  templateUrl: './see-tracks.component.html',
  styleUrl: './see-tracks.component.css',
})
export class SeeTracksComponent implements OnInit {

  public tracks: Track[] = [];

  constructor(private trackService: TrackService, private messageService: MessageService, private title: Title,
    private authorService: AuthorService, private playlistService: PlaylistService) { }

  ngOnInit(): void {
    this.title.setTitle("Tracks")

    this.trackService.getTracks().subscribe({
      error: err => this.messageService.add({ closable: true, summary: "Error while loading a tracks", detail: err.error, severity: "error" }),

      next: (tracksResp: any) => {

        this.authorService.getAuthorsByIds(tracksResp.map((x: any) => x.authorId))
          .subscribe({

            next: (authors: any) => {
              this.playlistService.getPlaylists(tracksResp.map((x: any) => x.playlistId)).subscribe({
                next: (playlists: any) => {
                  for (let i = 0; i < tracksResp.length; i++)
                    this.tracks.push({
                      "title": tracksResp[i].title, "audioUrl": tracksResp[i].audioUrl, "author": authors[i].name,
                      "playlist": playlists[i].name
                    })
                }
              })

            }

          });

      }
    });
  }

  public playTrack(track: Track): void {
    AudioComponent.playAudio(track);
  }

}