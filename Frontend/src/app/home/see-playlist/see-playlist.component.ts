import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AudioService } from '../../services/audio/audio.service';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { Playlist } from './playlist';
import { Track } from '../see-tracks/track';

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
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params: any) => {
      const playlistId = params['id'] || null;
      console.log(playlistId);
      
      if (playlistId) {
        this.loadPlaylist(parseInt(playlistId));
      }
    });
  }

  private loadPlaylist(playlistId: number): void {
    this.playlistService.getPlaylistsById(playlistId).subscribe((playlist: any) => {
      this.playlist = playlist;
    });
  }


  public playTrack(index: number): void {
    this.audioService.setTracks(this.tracks, index);
  }
}
