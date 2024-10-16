import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly KEY_NAME = 'token';

  constructor() { }

  public saveAuthToken(token: string): void{
    localStorage.setItem(this.KEY_NAME, token);  
  }

  public getAuthToken(): string | null{
    return localStorage.getItem(this.KEY_NAME);
  }

  public deleteAuthToken(): void{
    localStorage.removeItem(this.KEY_NAME); 
  }

  
}
