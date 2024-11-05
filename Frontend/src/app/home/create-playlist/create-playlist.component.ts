import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormArray } from '@angular/forms';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { AuthorService } from '../../services/author/author.service';
import { MessageService } from 'primeng/api';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-create-playlist',
  templateUrl: './create-playlist.component.html',
  styleUrl: './create-playlist.component.css'
})
export class CreatePlaylistComponent implements OnInit {
  public playlistForm: FormGroup;

  public playlistTypes: string[] = [];

  public authors: string[] = [];

  constructor(
    private fb: FormBuilder, 
    private playlistService:PlaylistService, 
    private authorService: AuthorService,
    private messageService: MessageService, 
    private title: Title
  ) {
    this.playlistForm = this.fb.group({
      title: ['', Validators.required],
      cover: [null],
      playlistType: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.title.setTitle("Create Playlist")
    this.playlistService.getPlaylistTypes().subscribe({
      next: (types: any) => {
        this.playlistTypes = types;
      }
    })

  }

  public searchAuthors(event: any) {
    this.authorService.getAuthorsBySymbol(event.query).subscribe({
      next: (authors: any) => {
        this.authors = authors.map((a: any) => a.name);
      }
    })
  }

  onSubmit(): void {
    if (this.playlistForm.valid) {
      const formData = new FormData();
      formData.append("cover", this.playlistForm.get("cover")?.value);
      formData.append("name", this.playlistForm.get("title")?.value);
      formData.append("playlistType", this.playlistForm.get("playlistType")?.value);

      this.playlistService.createPlaylist(formData).subscribe({
        next: (data) => this.messageService.add({ closable: true, detail: "Playlist created", severity: "success" }),
        error: (err) => this.messageService.add({ closable: true, detail: err.error, severity: "error", summary: "Something went wrong" })
      });
    }
  }

  onCoverSelected(event: any): void {
    const input = event.target as HTMLInputElement;

    if (input && input.files && input.files.length > 0) {
      const file = input.files[0];
      this.playlistForm.patchValue({ cover: file });
    }
  }
}
