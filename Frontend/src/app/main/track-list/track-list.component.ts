import { Component, OnDestroy } from '@angular/core';
import { AudioService } from '../../services/audio/audio.service';
import { Track } from './track';
import { Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TrackService } from '../../services/track/track.service';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-track-list',
  templateUrl: './track-list.component.html',
  styleUrl: './track-list.component.css',
  providers: [ConfirmationService]
})
export class TrackListComponent {

  protected tracks: Track[] = [];
  protected canBeDeleted: boolean = false;
  protected canBeRenamed: boolean = false;
  protected newTitle: string = "";
  protected addTrackDialogVisible: boolean = false;
  protected playlists: any[] = [];
  protected isPlaylistsNotFound: boolean = false;
  protected tracksNotFound: boolean = false;

  protected totalTracks: number = 0;
  protected totalPages: number = 0;
  protected pageSize: number = 5;
  protected currentPage: number = 0;

  protected notDisplayPagination: boolean = true;

  private indexOfEditingTrack: number = -1;
  private selectedTrackId: number = 0;
  private funcToDeleteTrackFromService: Function = (id: number) => { };
  private funcToGetTracksFromService: Function = (page: number) => {};
  private warningMessageToDisplayInConfirmDialog: string = "Do you want to do this?";


  constructor(
    private audioService: AudioService,
    private router: Router,
    private confirmationService: ConfirmationService,
    private trackService: TrackService,
    private playlistService: PlaylistService,
    private messageService: MessageService
  ) { }

  public setTracks(funcToGetTracks: Function): void {
    this.funcToGetTracksFromService = funcToGetTracks;
    this.loadTracks(this.funcToGetTracksFromService(0));
  }
  
  private loadTracks(resp: Observable<Object>){
    resp.subscribe({
      next: (tracks: any) => this.populateTracks(tracks),
      error: (error: any) => this.tracksNotFound = true
    });
  }
  
  private populateTracks(tracks: any){
    if (tracks == null) return;
        
    this.makeTracksEmpty();
    tracks.content.forEach((track: any) => {
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
    
    if(tracks.last && tracks.first){
      this.notDisplayPagination = true;
    }
    else{
      this.totalTracks = tracks.totalElements;
      this.totalPages = tracks.totalPages;  
      this.notDisplayPagination = false;
    }
  }

  public makeTracksEmpty(): void{
    this.tracks = [];
    this.totalPages = 0;
    this.totalPages = 0;
  }

  protected onPageChange(event: any): void {
    this.currentPage = event.page;
    this.loadTracks(this.funcToGetTracksFromService(event.page));
  }

  protected playTrack(index: number): void {
    this.audioService.setTracks(this.tracks, index);
  }

  protected seeAlbum(value: number): void {
    this.router.navigate(["/album/" + value]);
  }

  protected seeAuthor(value: number): void {
    this.router.navigate(["author/" + value]);
  }

  public onDelete(warningMessage: string, funcToDeleteTrack: Function) {
    this.canBeDeleted = true;
    this.funcToDeleteTrackFromService = funcToDeleteTrack;
    this.warningMessageToDisplayInConfirmDialog = warningMessage;
  }

  public makeUpdatable(value: boolean) {
    this.canBeRenamed = value;
  }

  protected confirmDeletionOfTrack(id: number) {
    this.confirmationService.confirm({
      message: this.warningMessageToDisplayInConfirmDialog,
      header: "Confirmation",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        this.funcToDeleteTrackFromService(id)
        this.tracks.splice(this.tracks.findIndex(t => t.id == id), 1);
      },
      reject: () => { }
    });
  }

  protected updateTrackTitle(track: Track) {
    if (this.canBeRenamed)
      this.trackService.updateTrackTitle(track.id, this.newTitle).subscribe(() => {
        this.messageService.add({ severity: "success", summary: "You have updated the track title", closable: true });
        track.title = this.newTitle;
        this.makeTrackEditableOrNot(track);
      });
  }

  protected makeTrackEditableOrNot(track: Track) {
    var isTheSameIndex = this.indexOfEditingTrack == track.id;

    if (this.indexOfEditingTrack != -1 && !isTheSameIndex) return

    this.newTitle = ""
    track.isEditing = !track.isEditing;

    this.indexOfEditingTrack = isTheSameIndex ? -1 : track.id;
  }

  protected makeEditDialogVisible(id: number) {
    this.playlistService.getPlaylistsByOwner().subscribe({
      next: (playlists: any) => this.playlists = playlists,
      error: () => this.isPlaylistsNotFound = true
    })
    this.selectedTrackId = id;
    this.addTrackDialogVisible = true;
  }

  protected addTrackToPlaylist(playlistId: number) {
    this.playlistService.addTrackToPlaylist(playlistId, this.selectedTrackId).subscribe({
      next: () => {
        this.messageService.add({ closable: true, summary: "Track has been added to playlist", severity: "success" })
        this.addTrackDialogVisible = false;
      },
      error: () => this.messageService.add({ closable: true, summary: "Already in playlist", severity: "error" })
    })
  }

}
