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
      file: [null]
    });
  }
  
  ngOnInit(): void {
    this.title.setTitle("Create Author");
  }
  
  public onSubmit(): void {
    if (this.authorFormGroup.valid){
      var formData = new FormData();
      formData.append("name", this.authorFormGroup.get("name")?.value);
      var file = this.authorFormGroup.get("file")?.value;
      if(file){
        formData.append("file", file);

      }
  
      this.authorService.createAuthor(formData).subscribe({
        next: (data) => this.messageService.add({ closable: true, detail: "You have created the author", severity: "success" }),
        error: (err) => this.messageService.add({ closable: true, detail: err.error, summary: "Something went wrong", severity: "error" })
      })
    }

  }

  onFileChange($event: Event) {
    const input = $event.target as HTMLInputElement;

    if (input && input.files && input.files.length > 0) {
      const file = input.files[0];
      this.authorFormGroup.patchValue({ "file": file });
    }
  }
}
