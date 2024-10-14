import { Component, inject } from '@angular/core';
import { LoginService } from '../services/login-service.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  public username: string = '';

  public password: string = '';

  private service: LoginService = inject(LoginService);
  
  onSubmit(): void{ 
    this.service.loginUser(this.username, this.password);
  }
}
