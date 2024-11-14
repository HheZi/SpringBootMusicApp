import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AudioService } from '../../services/audio/audio.service';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { Playlist } from './playlist';
import { Track } from '../see-tracks/track';
import { TrackService } from '../../services/track/track.service';
import { Title } from '@angular/platform-browser';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-see-playlist',
  templateUrl: './see-playlist.component.html',
  styleUrl: './see-playlist.component.css'
})
export class SeePlaylistComponent {
  public playlist: Playlist = {id: 0, imageUrl:"", name: "", numberOfTrack: 0, playlistType: ""};
  public tracks: Track[] = [];
  public isOwnerOfPlaylist = false;

  constructor(
    private route: ActivatedRoute,
    private playlistService: PlaylistService,
    private audioService: AudioService,
    private trackService: TrackService,
    private title: Title,
    private messageService: MessageService
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
      this.title.setTitle(this.playlist.name);
      
      this.trackService.getTrackInPlaylist(playlistId).subscribe((val: any) =>{
        this.playlist.numberOfTrack = val;
      });

      this.playlistService.getIsUserIsOwnerOfPlaylist(this.playlist.id).subscribe((resp: any) => {
        this.isOwnerOfPlaylist = resp;
      });
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

  public deleteTrackFromPlaylist(trackId: number){
    this.trackService.deleteTrack(trackId).subscribe(() => {
      this.messageService.add({closable: true, severity: "success", data: "Track deleted"});
      this.playlist.numberOfTrack--;
      this.tracks.splice(this.tracks.findIndex(t => t.id == trackId), 1);
    });
  }

  public playTrack(index: number): void {
    this.audioService.setTracks(this.tracks, index);
  }
}
