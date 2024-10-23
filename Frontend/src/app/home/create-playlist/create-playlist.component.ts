import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormArray } from '@angular/forms';

@Component({
  selector: 'app-create-playlist',
  templateUrl: './create-playlist.component.html',
  styleUrl: './create-playlist.component.css'
})
export class CreatePlaylistComponent {
  playlistForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.playlistForm = this.fb.group({
      title: ['', Validators.required],
      artist: ['', Validators.required],
      cover: [null],
      tracks: this.fb.array([])
    });
  }

  get tracks(): FormArray {
    return this.playlistForm.get('tracks') as FormArray;
  }

  addTrack(): void {
    this.tracks.push(this.fb.group({
      title: ['', Validators.required],
      audioUrl: ['', Validators.required]
    }));
  }

  removeTrack(index: number): void {
    this.tracks.removeAt(index);
  }

  onSubmit(): void {
    if (this.playlistForm.valid) {
      console.log('Playlist Created:', this.playlistForm.value);
      
    }
  }

  onCoverSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = e => this.playlistForm.patchValue({ cover: e.target?.result });
      reader.readAsDataURL(file);
    }
  }
}
