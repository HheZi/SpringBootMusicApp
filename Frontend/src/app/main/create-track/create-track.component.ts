import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthorService } from '../../services/author/author.service';
import { AlbumService } from '../../services/album/album.service';
import { TrackService } from '../../services/track/track.service';
import { MessageService } from 'primeng/api';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-create-track',
  templateUrl: './create-track.component.html',
  styleUrl: './create-track.component.css'
})
export class CreateTrackComponent implements  OnInit{

  public trackForm: FormGroup;
  
  public albums: string[] = []; 

  constructor(private fb: FormBuilder, private authorService: AuthorService, 
    private albumService: AlbumService, private trackService: TrackService, private messageService: MessageService,
    private title: Title) {
    

    this.trackForm = this.fb.group({
      title: ['', Validators.required],
      audio: [null, Validators.required],
      album: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.title.setTitle("Create Track");
  }
  
  onFileSelected(event: any): void {
    const file = event.files[0];
    this.trackForm.patchValue({ audio: file });
  }
  
  onSubmit(): void {
    if (this.trackForm.valid) {
      const formData = new FormData();
      formData.append("title", this.trackForm.get("title")?.value);
      formData.append("audio", this.trackForm.get("audio")?.value);
      formData.append("albumId", this.trackForm.get("album")?.value.id);
      
      console.log(formData);
      this.trackService.createTracks(formData).subscribe({
        next: (resp) => this.messageService.add({closable: true, detail: "The track has been created", severity: 'success'}),
        error: (err) => this.messageService.add({closable: true, severity: "error", summary: "Something went wrong"})
      });
    }
  }
  
  public searchAlbum(event: any): void{
    this.albumService.getAlbumsBySymbol(event.query).subscribe({
      next: (resp: any) => this.albums = resp
    });
  }

}
