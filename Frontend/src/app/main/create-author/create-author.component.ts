import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { AuthorService } from '../../services/author/author.service';
import { MessageService } from 'primeng/api';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-create-author',
  templateUrl: './create-author.component.html',
  styleUrl: './create-author.component.css'
})
export class CreateAuthorComponent implements OnInit{

  public authorFormGroup: FormGroup;

  constructor(
    private fb: FormBuilder, 
    private title: Title,
    private authorService: AuthorService,
    private messageService: MessageService
  ) {
    this.authorFormGroup = this.fb.group({
      name: ['', Validators.required],
      cover: [null]
    });
  }
  
  ngOnInit(): void {
    this.title.setTitle("Create Author");
  }
  
  public onSubmit(): void {
    if (this.authorFormGroup.valid){
      var formData = new FormData();
      formData.append("name", this.authorFormGroup.get("name")?.value);
      var file = this.authorFormGroup.get("cover")?.value;
      if(file){
        formData.append("cover", file);
      }
  
      this.authorService.createAuthor(formData).subscribe({
        next: (data) => this.messageService.add({ closable: true, summary: "You have created the author", severity: "success" }),
        error: (err) => {
          err.error.forEach((err: any) => {
            this.messageService.add({closable: true, severity: "error", summary: "Error while creating a track", detail: err})
          });
        }
      })
    }

  }

  onFileChange($event: Event) {
    const input = $event.target as HTMLInputElement;

    if (input && input.files && input.files.length > 0) {
      const file = input.files[0];
      this.authorFormGroup.patchValue({ "cover": file });
    }
  }
}
