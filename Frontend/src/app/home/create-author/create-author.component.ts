import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { AuthorService } from '../../services/author/author.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-create-author',
  templateUrl: './create-author.component.html',
  styleUrl: './create-author.component.css'
})
export class CreateAuthorComponent {
  public name: string = '';
  
  constructor(
    private title: Title, 
    private authorService: AuthorService, 
    private messageService: MessageService
  ){
    title.setTitle("Create Author")
  }

  public onSubmit(): void{
    this.authorService.createAuthor({name: this.name}).subscribe({
      next: (data) => this.messageService.add({closable: true, detail: "You have created the author", severity: "success"}),
      error: (err) => this.messageService.add({closable: true, detail: err.error, summary: "Something went wrong", severity: "error"})
    })
  }
}
