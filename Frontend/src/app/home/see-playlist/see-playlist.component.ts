import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AudioService } from '../../services/audio/audio.service';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { Playlist } from './playlist';
import { Track } from '../see-tracks/track';
import { TrackService } from '../../services/track/track.service';
import { Title } from '@angular/platform-browser';
import { MessageService } from 'primeng/api';
import { AppConts } from '../../app.consts';

@Component({
  selector: 'app-see-playlist',
  templateUrl: './see-playlist.component.html',
  styleUrl: './see-playlist.component.css'
})
export class SeePlaylistComponent {
  public playlist: Playlist = {id: 0, imageUrl:AppConts.BASE_URL + "/api/images/default", name: "", numberOfTrack: 0, playlistType: ""};
  public tracks: Track[] = [];
  public isOwnerOfPlaylist = false;

  public editDialogVisible = false;
  public editablePlaylist: Playlist = { id: 0, imageUrl: "", name: "", numberOfTrack: 0, playlistType: "" };
  private selectedFile: File | null = null;
  public previewImage: string | ArrayBuffer | null = null;

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
      this.tracks = [];
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

  onFileChange(event: any): void {
    this.selectedFile = event.target.files[0];
    if (this.selectedFile) {
      const reader = new FileReader();
      reader.onload = (e: any) => (this.previewImage = e.target.result);
      reader.readAsDataURL(this.selectedFile);
    }
  }

  saveChanges() {
    const formData = new FormData();
    formData.append('name', this.editablePlaylist.name);
    if (this.selectedFile) {
      formData.append('cover', this.selectedFile, this.selectedFile.name);
    }

    this.playlistService.updatePlaylist(this.playlist.id, formData).subscribe(
      () => {
        this.messageService.add({ severity: 'success', summary: 'Playlist updated successfully' });
        this.editDialogVisible = false;
        this.loadPlaylist(this.playlist.id);
      },
    );
  }
}
