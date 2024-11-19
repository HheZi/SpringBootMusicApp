import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthorService } from '../../services/author/author.service';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { TrackService } from '../../services/track/track.service';
import { Track } from '../see-tracks/track';
import { AudioService } from '../../services/audio/audio.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-see-author',
  templateUrl: './see-author.component.html',
  styleUrl: './see-author.component.css'
})
export class SeeAuthorComponent implements OnInit{
  
  
  public author: any = { id: 0, name: '', imageUrl: '' };
  public playlists: any[] = [];
  public tracks: Track[] = [];
  
  constructor(
    private activeRoute: ActivatedRoute,
    private authorService: AuthorService,
    private playlistService: PlaylistService,
    private trackService: TrackService,
    private messageService: MessageService,
    private audioService: AudioService,
    private router: Router,
    private title: Title
  ) {}
  
  ngOnInit(): void {
    const authorId = this.activeRoute.snapshot.paramMap.get('id');
    if (authorId) {
      this.loadAuthor(parseInt(authorId));
    }
  }
  
  private loadAuthor(authorId: number): void {
    this.authorService.getAuthorsById(authorId).subscribe((author) => {
      this.author = author;
      this.title.setTitle(this.author.name);
    });
    
    this.trackService.getTracksByAuthorId(authorId).subscribe((tracksResp: any) => {
      tracksResp.forEach((track: any) => {
        this.tracks.push({
          id: track.id,
          title: track.title,
          audioUrl: track.audioUrl,
          author: track.author.name,  
          authorId: track.author.id,
          imageUrl: track.playlist.imageUrl,  
          playlist: track.playlist.name,  
          playlistId: track.playlist.id,
          isNowPlaying: false,
          duration: track.duration,
          isEditing: false
        });
      });
      var ids = this.tracks.map(t => t.playlistId);
      
      this.playlistService.getPlaylistsByIds(ids).subscribe((playlists: any) =>{
        this.playlists = playlists;
      });
    });
    
  }
  
  editAuthor(): void {
    console.log('Edit author:', this.author);
  }
  
  deletePlaylist(playlistId: number): void {
    this.playlistService.deletePlaylist(playlistId).subscribe(() => {
      this.messageService.add({
        severity: 'success',
        summary: 'Playlist Deleted',
      });
      this.playlists = this.playlists.filter((p) => p.id !== playlistId);
    });
  }
  
  deleteTrack(trackId: number): void {
    this.trackService.deleteTrack(trackId).subscribe(() => {
      this.messageService.add({
        severity: 'success',
        closable: true,
        summary: 'Track Deleted',
      });
      this.tracks = this.tracks.filter((t) => t.id !== trackId);
    });
  }
  
  public playTrack(trackId: number) {
    this.audioService.setTracks(this.tracks, trackId);
  }

  public seePlaylist(playlistId: number) {
    this.router.navigate(["playlist/see/"+playlistId]);
  }
}
