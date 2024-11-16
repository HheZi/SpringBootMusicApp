import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppConts } from '../../app.consts';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private readonly TOKEN_KEY_NAME = 'token';
  
  private readonly  REFRESH_TOKEN_KEY_NAME = "refreshToken";
  
  constructor(private httpClient: HttpClient) { }
  
  public saveAuthToken(value: any): void{
    localStorage.setItem(this.TOKEN_KEY_NAME, value.token);  
    localStorage.setItem(this.REFRESH_TOKEN_KEY_NAME, value.refreshToken);
  }
  
  public saveJwtToken(tokenToSave: any){
    localStorage.setItem(this.TOKEN_KEY_NAME, tokenToSave.token);
    localStorage.setItem(this.REFRESH_TOKEN_KEY_NAME, tokenToSave.refreshToken);
  }
  
  public refreshToken(): Observable<Object>{
    return this.httpClient.post(AppConts.BASE_URL + "/api/auth/refresh", {refreshToken: this.getRefreshToken()});
  }
  
  public getAuthToken(): string | null{
    return localStorage.getItem(this.TOKEN_KEY_NAME);
  }
  
  private getRefreshToken(): string |  null{
    return localStorage.getItem(this.REFRESH_TOKEN_KEY_NAME);
  }
  
  public logout() {
    localStorage.removeItem(this.REFRESH_TOKEN_KEY_NAME);
    localStorage.removeItem(this.TOKEN_KEY_NAME);
  }
}
