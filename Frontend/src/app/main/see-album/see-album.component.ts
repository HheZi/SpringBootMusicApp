import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { AudioService } from '../../services/audio/audio.service';
import { AlbumService } from '../../services/album/album.service';
import { Track } from '../track-list/track';
import { TrackService } from '../../services/track/track.service';
import { Title } from '@angular/platform-browser';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AppConts } from '../../app.consts';
import { Album } from './album';
import { TrackListComponent } from '../track-list/track-list.component';

@Component({
  selector: 'app-see-album',
  templateUrl: './see-album.component.html',
  styleUrl: './see-album.component.css',
  providers: [ConfirmationService]
})
export class SeeAlbumComponent {
  public album: Album = { id: 0, imageUrl: "", name: "", numberOfTrack: 0, albumType: "", releaseDate: null, totalDuration: ''};
  public isOwnerOfAlbum = false;
  public isNotFound = false;

  @ViewChild(TrackListComponent) trackList!: TrackListComponent;

  public editDialogVisible = false;
  public editableAlbum: Album = { id: 0, imageUrl: "", name: "", numberOfTrack: 0, albumType: "", releaseDate: null,  totalDuration: ''};
  private selectedFile: File | null = null;
  public previewImage: string | ArrayBuffer | null = null;

  constructor(
    private activeRoute: ActivatedRoute,
    private router: Router,
    private albumService: AlbumService,
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
          this.trackList.onDelete('Delete this track from album?',(trackId: number) => {
            this.trackService.deleteTrack(trackId).subscribe(() => {
              this.messageService.add({ closable: true, severity: "success", summary: "Track deleted"});
              this.album.numberOfTrack--;
            });
          });
          this.trackList.makeUpdatable();
        });
        
        this.trackService.getTracksByAlbumId(albumId).subscribe((tracks: any) => {
          this.trackList.setTracks(tracks);
          this.trackService.getDurationByIds(tracks.map((t: any) => t.id)).subscribe({
            next: (resp: any) => this.album.totalDuration = resp.duration
          })
        });
        
        
      },
      error: () => this.isNotFound = true
    });
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
      this.router.navigate(["/home"]);
    });
  }

  public removeCover() {
    this.albumService.deleteCover(this.album.id).subscribe(() => {
      this.loadAlbum(this.album.id);
    });
  }

  public confirmDeletionOfAlbum() {
    this.confirmationService.confirm({
      message: "Do you want to delete?",
      header: "Confirmation",
      icon: "pi pi-exclamation-triangle",
      accept: () => this.deleteAlbum()
    });
  }

}
