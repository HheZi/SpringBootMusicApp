import { Component } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { AudioService } from '../../services/audio/audio.service';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { Playlist } from './playlist';
import { Track } from '../see-tracks/track';
import { TrackService } from '../../services/track/track.service';
import { Title } from '@angular/platform-browser';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AppConts } from '../../app.consts';

@Component({
  selector: 'app-see-playlist',
  templateUrl: './see-playlist.component.html',
  styleUrl: './see-playlist.component.css',
  providers: [ConfirmationService] 
})
export class SeePlaylistComponent {
  public playlist: Playlist = {id: 0, imageUrl:"", name: "", numberOfTrack: 0, playlistType: "", releaseDate: null};
  public tracks: Track[] = [];
  public isOwnerOfPlaylist = false;

  public editDialogVisible = false;
  public editablePlaylist: Playlist = { id: 0, imageUrl: "", name: "", numberOfTrack: 0, playlistType: "", releaseDate: null};
  private selectedFile: File | null = null;
  public previewImage: string | ArrayBuffer | null = null;

  constructor(
    private activeRoute: ActivatedRoute,
    private route: Router,
    private playlistService: PlaylistService,
    private audioService: AudioService,
    private trackService: TrackService,
    private title: Title,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.activeRoute.queryParams.subscribe((params: any) => {
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

  private deleteTrackFromPlaylist(trackId: number){
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

  public saveChanges() {
    const formData = new FormData();
    formData.append('name', this.editablePlaylist.name);
    if (this.selectedFile) {
      formData.append('cover', this.selectedFile, this.selectedFile.name);
    }
    
    formData.append("releaseDate", this.parseReleaseDate())

    this.playlistService.updatePlaylist(this.playlist.id, formData).subscribe(
      () => {
        this.messageService.add({ severity: 'success', summary: 'Playlist updated successfully' });
        this.editDialogVisible = false;
        this.loadPlaylist(this.playlist.id);
      },
    );
  }

  private parseReleaseDate(): string{
    if (this.editablePlaylist.releaseDate){
      var d = this.editablePlaylist.releaseDate as Date;
      d.setDate(d.getDate()+1)
      return d.toISOString().split("T")[0];
    }
    return "";
  }

  private deletePlaylist(){
    this.playlistService.deletePlaylist(this.playlist.id).subscribe(() => {
      this.messageService.add({closable: true, summary: "The playlist deleted", severity: "success"});
      this.route.navigate(["/tracks/see"]);
    });
  }
  public removeCover(){
    this.playlistService.deleteCover(this.playlist.id).subscribe(() => {
      this.loadPlaylist(this.playlist.id);
    });
  }

  public confirmDeletionOfPlaylist(){
    this.confirmDeletion(() => this.deletePlaylist());
  }

  public confirmDeletionOfTrack(id: number){
    this.confirmDeletion(() => this.deleteTrackFromPlaylist(id));
  }


  private confirmDeletion(func: Function){
    this.confirmationService.confirm({
      message: "You want to delete?",
      header: "Confirmation",
      icon: "pi pi-exclamation-triangle",
      accept: func
    });
  } 

}
