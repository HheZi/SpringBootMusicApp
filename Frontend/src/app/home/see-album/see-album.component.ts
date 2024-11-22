import { Component } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { AudioService } from '../../services/audio/audio.service';
import { AlbumService } from '../../services/album/album.service';
import { Track } from '../home-page/track';
import { TrackService } from '../../services/track/track.service';
import { Title } from '@angular/platform-browser';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AppConts } from '../../app.consts';
import { Album } from './album';

@Component({
  selector: 'app-see-album',
  templateUrl: './see-album.component.html',
  styleUrl: './see-album.component.css',
  providers: [ConfirmationService]
})
export class SeeAlbumComponent {
  public album: Album = { id: 0, imageUrl: "", name: "", numberOfTrack: 0, albumType: "", releaseDate: null };
  public tracks: Track[] = [];
  public isOwnerOfAlbum = false;
  public isNotFound = false;

  public editDialogVisible = false;
  public editableAlbum: Album = { id: 0, imageUrl: "", name: "", numberOfTrack: 0, albumType: "", releaseDate: null };
  private selectedFile: File | null = null;
  public previewImage: string | ArrayBuffer | null = null;
  public newTitle: string = "";
  public indexOfEditingTrack: number = -1;

  constructor(
    private activeRoute: ActivatedRoute,
    private router: Router,
    private albumService: AlbumService,
    private audioService: AudioService,
    private trackService: TrackService,
    private title: Title,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) { }

  ngOnInit(): void {
    const albumId = this.activeRoute.snapshot.paramMap.get('id');
    if (albumId) {
      this.loadAlbum(parseInt(albumId));
    }
  }

  private loadAlbum(albumId: number): void {
    this.albumService.getAlbumsById(albumId).subscribe({
      next: (album: any) => {
        this.album = album;
        this.title.setTitle(this.album.name);

        this.trackService.getTrackInAlbum(albumId).subscribe((val: any) => {
          this.album.numberOfTrack = val;
        });

        this.albumService.getIsUserIsOwnerOfAlbum(this.album.id).subscribe((resp: any) => {
          this.isOwnerOfAlbum = resp;
        });

        this.trackService.getTracksByAlbumId(albumId).subscribe((tracks: any) => {
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
        });
      },
      error: () => this.isNotFound = true
    });
}

  private deleteTrackFromAlbum(trackId: number) {
    this.trackService.deleteTrack(trackId).subscribe(() => {
      this.messageService.add({ closable: true, severity: "success", summary: "Track deleted" });
      this.album.numberOfTrack--;
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
    formData.append('name', this.editableAlbum.name);
    if (this.selectedFile) {
      formData.append('cover', this.selectedFile, this.selectedFile.name);
    }

    formData.append("releaseDate", this.parseReleaseDate())

    this.albumService.updateAlbum(this.album.id, formData).subscribe(
      () => {
        this.messageService.add({ severity: 'success', summary: 'Album updated successfully' });
        this.editDialogVisible = false;
        this.loadAlbum(this.album.id);
      },
    );
  }

  private parseReleaseDate(): string {
    if (this.editableAlbum.releaseDate) {
      var d = this.editableAlbum.releaseDate as Date;
      d.setDate(d.getDate() + 1)
      return d.toISOString().split("T")[0];
    }
    return "";
  }

  private deleteAlbum() {
    this.albumService.deleteAlbum(this.album.id).subscribe(() => {
      this.messageService.add({ closable: true, summary: "The album deleted", severity: "success" });
      this.router.navigate(["/tracks"]);
    });
  }
  public removeCover() {
    this.albumService.deleteCover(this.album.id).subscribe(() => {
      this.loadAlbum(this.album.id);
    });
  }

  public confirmDeletionOfAlbum() {
    this.confirmDeletion(() => this.deleteAlbum());
  }

  public confirmDeletionOfTrack(id: number) {
    this.confirmDeletion(() => this.deleteTrackFromAlbum(id));
  }


  private confirmDeletion(func: Function) {
    this.confirmationService.confirm({
      message: "You want to delete?",
      header: "Confirmation",
      icon: "pi pi-exclamation-triangle",
      accept: func
    });
  }

  public updateTrackTitle(track: Track) {
    this.trackService.updateTrackTitle(track.id, this.newTitle).subscribe(() => {
      this.messageService.add({ severity: "success", summary: "You have updated the track title", closable: true });
      track.title = this.newTitle;
      this.makeTrackEditableOrNot(track);
    });
  }

  public makeTrackEditableOrNot(track: Track) {
    var isTheSameIndex = this.indexOfEditingTrack == track.id;

    if (this.indexOfEditingTrack != -1 && !isTheSameIndex) return

    this.newTitle = ""
    track.isEditing = !track.isEditing;

    this.indexOfEditingTrack = isTheSameIndex ? -1 : track.id;
  }

  public seeAuthor(value: number) {
    this.router.navigate(["author/" + value]);
  }

}
