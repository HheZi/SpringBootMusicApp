import { Component, OnDestroy } from '@angular/core';
import { AudioService } from '../../services/audio/audio.service';
import { Track } from './track';
import { Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TrackService } from '../../services/track/track.service';

@Component({
  selector: 'app-track-list',
  templateUrl: './track-list.component.html',
  styleUrl: './track-list.component.css',
  providers: [ConfirmationService]
})
export class TrackListComponent {
  protected tracks: Track[] = [];
  protected isModifiableList: boolean = false;
  protected newTitle: string = "";
  protected indexOfEditingTrack: number = -1;

  constructor(
    private audioService: AudioService,
    private router: Router,
    private confirmationService: ConfirmationService,
    private trackService: TrackService,
    private messageService: MessageService
  ){}

  public setTracks(tracks: any): void{
    this.tracks = [];
      tracks.forEach((track: any) => {
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
  }

  protected playTrack(index: number): void {
    this.audioService.setTracks(this.tracks, index);
  }

  protected seeAlbum(value: number): void{
    this.router.navigate(["/album/"+ value]);
  }

  protected seeAuthor(value: number): void{
    this.router.navigate(["author/" + value]);
  }

  public setModifiable(value: boolean){
    this.isModifiableList = value;
  }

  private deleteTrackFromAlbum(trackId: number) {
    this.trackService.deleteTrack(trackId).subscribe(() => {
      this.messageService.add({ closable: true, severity: "success", summary: "Track deleted" });
      this.tracks.splice(this.tracks.findIndex(t => t.id == trackId), 1);
    });
  }

  protected confirmDeletionOfTrack(id: number) {
    this.confirmationService.confirm({
      message: "Do you want to delete the track?",
      header: "Confirmation",
      icon: "pi pi-exclamation-triangle",
      accept: () => this.deleteTrackFromAlbum(id)
    });
  }

  protected updateTrackTitle(track: Track) {
    if(this.isModifiableList)
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

}
