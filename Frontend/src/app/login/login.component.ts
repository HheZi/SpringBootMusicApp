import { Component } from '@angular/core';
import { LoginService } from '../services/login/login-service.service';
import { MessageService } from 'primeng/api';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent{

  public username: string = '';

  public password: string = '';
  
  constructor(private service: LoginService, private router: Router, 
    private messageService: MessageService, private authService: AuthService){}

  onSubmit(): void{ 
    this.service.loginUser(this.username, this.password).subscribe({
      next: (token: string) =>{
        this.authService.saveAuthToken(token);
        this.router.navigate(["/"]);
      },
      error: err => {
        this.messageService.add({severity: "error", summary: "Error", detail: "Bad credential", closable: true})
      },
    });

  }

  public navigateToRegistration(): void{
    this.router.navigate(["registration"])
  }

}
