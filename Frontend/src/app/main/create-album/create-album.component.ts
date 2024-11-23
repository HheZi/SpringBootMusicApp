import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormArray } from '@angular/forms';
import { AlbumService } from '../../services/album/album.service';
import { AuthorService } from '../../services/author/author.service';
import { MessageService } from 'primeng/api';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-create-album',
  templateUrl: './create-album.component.html',
  styleUrl: './create-album.component.css'
})
export class CreateAlbumComponent implements OnInit {
  public albumForm: FormGroup;

  public albumTypes: string[] = [];

  public authors: string[] = [];

  constructor(
    private fb: FormBuilder, 
    private albumService:AlbumService, 
    private authorService: AuthorService,
    private messageService: MessageService, 
    private title: Title
  ) {
    this.albumForm = this.fb.group({
      title: ['', Validators.required],
      cover: [null],
      releaseDate: [null],
      albumType: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.title.setTitle("Create Album")
    this.albumService.getAlbumTypes().subscribe({
      next: (types: any) => {
        this.albumTypes = types;
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
    if (this.albumForm.valid) {
      const formData = new FormData();

      if(this.albumForm.get("cover")?.value)
        formData.append("cover", this.albumForm.get("cover")?.value);
      
      formData.append("name", this.albumForm.get("title")?.value);
      formData.append("albumType", this.albumForm.get("albumType")?.value);
      formData.append("releaseDate", this.parseReleasDate());

      this.albumService.createAlbum(formData).subscribe({
        next: (data) => this.messageService.add({ closable: true, detail: "Album created", severity: "success" }),
        error: (err) => this.messageService.add({ closable: true, detail: err.error, severity: "error", summary: "Something went wrong" })
      });
    }
  }

  private parseReleasDate(): string{
    var d = this.albumForm.get("releaseDate")?.value as Date;
    d.setDate(d.getDate()+1)
    return d.toISOString().split("T")[0];
  }

  onCoverSelected(event: any): void {
    const input = event.target as HTMLInputElement;

    if (input && input.files && input.files.length > 0) {
      const file = input.files[0];
      this.albumForm.patchValue({ cover: file });
    }
  }

}
