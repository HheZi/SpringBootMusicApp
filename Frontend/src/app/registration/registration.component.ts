import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { RegistrationService } from '../services/registration/registration.service';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css'
})
export class RegistrationComponent implements OnInit{

  username: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';

  constructor(private messageService: MessageService, private service: RegistrationService, private router: Router, private title: Title) { }
  
  ngOnInit(): void {
    this.title.setTitle("Registration");
  }

  onSubmit() {
    if (this.password !== this.confirmPassword) {
      this.messageService.add({
        severity: 'error',
        summary: 'Registration Failed',
        detail: 'Passwords do not match!',
        closable: true
      });
      return;
    }

    this.service.registerUser(this.username, this.email, this.password)
    .subscribe({
      next: () => this.messageService.add({
        severity: 'success',
        summary: 'Registration Successful',
        detail: 'You have registered successfully!',
        closable: true
      }),
      error: (er) => {

          er.error.forEach((element: string) => {
            this.messageService.add({summary: "Invalid input", closable: true, detail: element, severity: "error"})
          });
        console.log(er);
        
      }
    });
  }

  public routeToLogin(): void {
    this.router.navigate(["login"]);
  }

}
