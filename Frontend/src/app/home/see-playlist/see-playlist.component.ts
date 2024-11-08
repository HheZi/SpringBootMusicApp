import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AudioService } from '../../services/audio/audio.service';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { Playlist } from './playlist';
import { Track } from '../see-tracks/track';
import { TrackService } from '../../services/track/track.service';

@Component({
  selector: 'app-see-playlist',
  templateUrl: './see-playlist.component.html',
  styleUrl: './see-playlist.component.css'
})
export class SeePlaylistComponent {
  public playlist: Playlist = {id: 0, imageUrl:"", name: ""};
  public tracks: Track[] = [];

  constructor(
    private route: ActivatedRoute,
    private playlistService: PlaylistService,
    private audioService: AudioService,
    private trackService: TrackService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params: any) => {
      const playlistId = params['id'] || null;

      if (playlistId) {
        this.loadPlaylist(parseInt(playlistId));
      }
    });
  }

  private loadPlaylist(playlistId: number): void {
    this.playlistService.getPlaylistsById(playlistId).subscribe((playlist: any) => {
      this.playlist = playlist;
    });
    this.trackService.getTracksByPlaylistId(playlistId).subscribe((tracks: any) =>{
      tracks.forEach((track: any) => {
        this.tracks.push({
          id: track.id,
          title: track.title,
          audioUrl: track.audioUrl,
          author: track.author.name,  
          authorId: track.author.id,
          imageUrl: track.playlist.imageUrl,  
          playlist: track.playlist.name,  
          playlistId: track.playlist.id,
          isNowPlaying: false
        });
      });
    })
  }


  public playTrack(index: number): void {
    this.audioService.setTracks(this.tracks, index);
  }
}
