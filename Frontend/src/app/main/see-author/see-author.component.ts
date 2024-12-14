import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthorService } from '../../services/author/author.service';
import { AlbumService } from '../../services/album/album.service';
import { TrackService } from '../../services/track/track.service';
import { Title } from '@angular/platform-browser';
import { Author } from './author';
import { TrackListComponent } from '../track-list/track-list.component';

@Component({
  selector: 'app-see-author',
  templateUrl: './see-author.component.html',
  styleUrl: './see-author.component.css'
})
export class SeeAuthorComponent implements OnInit {
  
  
  public author: Author = { id: 0, name: '', imageUrl: '', description: ''};
  public albums: any[] = [];
  public canModify: boolean = false;
  public updateDialog: boolean = false;
  public isNotFound: boolean = false;

  public editableAuthor: Author = { id: 0, name: '', imageUrl: '', description: ''};
  public previewImage: string | ArrayBuffer | null = null;
  private selectedFile: File | null = null;

  @ViewChild(TrackListComponent) trackList!: TrackListComponent;

  constructor(
    private activeRoute: ActivatedRoute,
    private authorService: AuthorService,
    private albumService: AlbumService,
    private trackService: TrackService,
    private messageService: MessageService,
    private router: Router,
    private title: Title
  ) { }

  ngOnInit(): void {
    const authorId = this.activeRoute.snapshot.paramMap.get('id');
    if (authorId) {
      this.loadAuthor(parseInt(authorId));
    }
  }

  private loadAuthor(authorId: number): void {
    this.authorService.getAuthorsById(authorId).subscribe({
      next: (author: any) => {
        this.author = author;
        this.editableAuthor.name = this.author.name;
        this.editableAuthor.description = this.author.description;
        this.title.setTitle(this.author.name);
        this.authorService.canModify(this.author.id).subscribe((resp: any) => {
          this.canModify = resp;
        })
        this.albumService.getAlbumsByAuthorId(this.author.id).subscribe((albums: any) => {
          this.albums = albums;

          this.trackList.setTracks((page: number) => this.trackService.getTracksByAlbumId(albums.map((a: any) => a.id), page));
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

  public openOrCloseDialog() {
    this.updateDialog = !this.updateDialog;
  }

  editAuthor(): void {
    var formData = new FormData();
    
    formData.append("name", this.editableAuthor.name);
    formData.append("description", this.editableAuthor.description);
    if (this.selectedFile) {
      formData.append("cover", this.selectedFile);
    }
    
    this.authorService.updateAuthor(formData, this.author.id).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Albums updated successfully' });
        this.updateDialog = false;
        this.loadAuthor(this.author.id);
      },
      error: (err: any) => err.error.forEach((err: any) => {
        this.messageService.add({ closable: true, summary: "Can't update the author", detail: err, severity: "error" })
      })
    })
  }
  
  protected removeImage() {
    this.authorService.deleteAuthorImage(this.author.id).subscribe({
      next: (resp: any) => {
        this.loadAuthor(this.author.id);
        this.messageService.add({closable: true, detail: "Image deleted", severity: "success"});
        this.updateDialog = false;
      },
      error: (error: any) => this.messageService.add({closable: true, detail: "Something went wrong", severity: "error"})
    })
  }
  
  public seeAlbum(albumId: number) {
    this.router.navigate(["album/" + albumId]);
  }
}
