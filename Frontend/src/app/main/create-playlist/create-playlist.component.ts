import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-create-playlist',
  templateUrl: './create-playlist.component.html',
  styleUrl: './create-playlist.component.css'
})
export class CreatePlaylistComponent {

  public playlistForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private title: Title,
    private playlistService: PlaylistService,
    private messageService: MessageService
  ){
    this.playlistForm = fb.group({
      name: ["", Validators.required],
      description: [""],
      cover: [null]
    });
    title.setTitle("Create Playlist");
  }

  public onCoverSelected($event: Event): void{
    var input = $event.target as HTMLInputElement;

    if (input && input.files && input.files.length > 0) {
      const file = input.files[0];
      this.playlistForm.patchValue({ cover: file });
    }
  }

  public onSubmit(): void{
    var formData = new FormData();

    formData.append("name", this.playlistForm.get("name")?.value);

    var description = this.playlistForm.get("description")?.value;
    var cover = this.playlistForm.get("cover")?.value;
    if(description){
      formData.append("description", description);
    }
    if(cover){
      formData.append("cover", cover);
    }

    this.playlistService.createPlaylist(formData).subscribe({
      next: () => {
        this.messageService.add({closable: true, summary: "The Playlist Created", severity: "success"});
      },
      error: (err) => {
        err.error.forEach((err: any) => {
          this.messageService.add({closable: true, severity: "error", summary: "Error while creating a playlist", detail: err})
        });
      }
    })
  }

}
