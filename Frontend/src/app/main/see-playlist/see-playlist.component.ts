import { Component, OnInit, ViewChild } from '@angular/core';
import { Playlist } from './playlist';
import { Title } from '@angular/platform-browser';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TrackListComponent } from '../track-list/track-list.component';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TrackService } from '../../services/track/track.service';
import { HttpHeaders } from '@angular/common/http';
import { formatNumber } from '@angular/common';

@Component({
  selector: 'app-see-playlist',
  templateUrl: './see-playlist.component.html',
  styleUrl: './see-playlist.component.css',
  providers: [ConfirmationService]
})
export class SeePlaylistComponent implements OnInit {

  protected playlist: Playlist = { id: 0, name: "", imageUrl: "", description: "", trackIds: [], numberOfTracks: 0, totalDuration: ''};
  protected editablePlaylist: Playlist = { id: 0, name: "", imageUrl: "", description: "", trackIds: [], numberOfTracks: 0, totalDuration: ''};
  protected isPlaylistNotFound: boolean = false;
  protected isTracksNotFound: boolean = false;

  protected canModify: boolean = false;
  protected editDialogeVisible: boolean = false
  private selectedFile: File | null = null;
  public previewImage: string | ArrayBuffer | null = null;

  @ViewChild(TrackListComponent) trackList!: TrackListComponent;

  constructor(
    private title: Title,
    private playlistService: PlaylistService,
    private trackService: TrackService,
    private activetedRoute: ActivatedRoute,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const albumId = this.activetedRoute.snapshot.paramMap.get('id');
    if (albumId)
      this.loadPlaylist(parseInt(albumId))
  }

  private loadPlaylist(id: number) {
    this.playlistService.getPlaylist(id).subscribe({
      next: (playlist: any) => {
        this.title.setTitle(playlist.name);
        this.playlist = playlist;
        this.editablePlaylist.description = playlist.description;

        if (this.playlist.trackIds.length != 0) {
          this.trackService.getTrackByIds(this.playlist.trackIds).subscribe({
            next: (tracksResp: any) => {
              this.trackService.getDurationByIds(tracksResp.map((t: any) => t.id)).subscribe(
                (resp: any) => this.playlist.totalDuration = resp.duration
              )

              this.trackList.setTracks(tracksResp);

              this.trackList.onDelete("Delete track from playlist?", (trackId: number) => {
                this.playlistService.deleteTrackFromPlaylist(this.playlist.id, trackId).subscribe({
                  next: () => {
                    this.messageService.add({closable: true, severity: "success", summary: "Track deleted from playlist"});
                    this.playlist.numberOfTracks--;
                  },
                  error: () => this.messageService.add({closable: true, severity: "error", summary: "Something went wrong"})
                })
              })
            }
          })
        }
        else{
          this.isTracksNotFound = true;
        }

        this.playlistService.getIsOwnerOfPlaylist(this.playlist.id).subscribe((resp: any) => this.canModify = resp)


      },
      error: () => this.isPlaylistNotFound = true
    })

  }

  onFileChange(event: any): void {
    this.selectedFile = event.target.files[0];
    if (this.selectedFile) {
      const reader = new FileReader();
      reader.onload = (e: any) => (this.previewImage = e.target.result);
      reader.readAsDataURL(this.selectedFile);
    }
  }

  protected saveChanges(): void{
    var formData = new FormData();

    if(this.editablePlaylist.name){
      formData.append("name", this.editablePlaylist.name);
    }

    formData.append("description", this.editablePlaylist.description);
    
    if(this.selectedFile){
      formData.append("cover", this.selectedFile);
    }

    this.playlistService.savePlaylist(formData, this.playlist.id).subscribe(() => {
      this.messageService.add({closable: true, summary: "Playlist updated", severity: "success"});
      this.loadPlaylist(this.playlist.id);    
      this.editDialogeVisible = false;
    });
  }

  private deletePlayist(){
    this.playlistService.deletePlaylist(this.playlist.id).subscribe({
      next: () => {
        this.messageService.add({closable: true, summary: "You have deleted the playlist", severity: "success"});
        this.router.navigate(['/home']);
      }
    })
  }

  protected confirmDeletionOfPlaylist(): void{
    this.confirmationService.confirm({
      message: "Do you want to delete?",
      header: "Confirmation",
      icon: "pi pi-exclamation-triangle",
      accept: () => this.deletePlayist()
    });
  }

  protected removeCover(): void{
    this.playlistService.deleteCover(this.playlist.id).subscribe({
      next: () => {
        this.loadPlaylist(this.playlist.id)
        this.editDialogeVisible = false;
      },
      error: () => this.messageService.add({closable: true, summary: "Something went wrong", severity: "error"})
    })
  }
}
