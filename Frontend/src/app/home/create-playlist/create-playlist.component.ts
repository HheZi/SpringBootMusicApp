import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormArray } from '@angular/forms';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { AuthorService } from '../../services/author/author.service';

@Component({
  selector: 'app-create-playlist',
  templateUrl: './create-playlist.component.html',
  styleUrl: './create-playlist.component.css'
})
export class CreatePlaylistComponent implements OnInit{
  playlistForm: FormGroup;
  
  public playlistTypes: string[] = [];

  public authors: string[] = [];

  constructor(private fb: FormBuilder, private playlistService: PlaylistService, private authorService: AuthorService) {
    this.playlistForm = this.fb.group({
      title: ['', Validators.required],
      author: [''],
      cover: [null],
      playlistType: ['', Validators.required],
      tracks: this.fb.array([])
    });
  }
  ngOnInit(): void {
    this.playlistService.getPlaylistTypes().subscribe({
      next: (types: any) => {
       this.playlistTypes = types;
      } 
    })
    
  }

  public searchAuthors(event: any){
    this.authorService.getAuthorsBySymbol(event.query).subscribe({
      next: (authors: any) => {
        this.authors = authors.map((a: any) => a.name);
      }
    })
  }
   
  get tracks(): FormArray { 
    return this.playlistForm.get('tracks') as FormArray; 
  } 
  
  addTrack(): void {
    this.tracks.push(this.fb.group({
      title: ['', Validators.required],
      audio: [null],
      author: ['']
    }));
  }
  
  removeTrack(index: number): void {
    this.tracks.removeAt(index);
  }

  deleteImage() {
    this.playlistForm.patchValue({"cover": null});
  }  
    
  onSubmit(): void {
    console.log('Playlist Created:', this.playlistForm.value);
    if (this.playlistForm.valid) {
      this.playlistService.createPlaylist(this.playlistForm);
    }
  }

  onFileSelected(event: any, index: number): void {
    const file = event.files[0]; 
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.tracks.at(index).patchValue({ audio: reader.result }); 
      };
      reader.readAsDataURL(file); 
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
