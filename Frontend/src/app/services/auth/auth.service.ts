import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppConts } from '../../app.consts';
import { BehaviorSubject, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly TOKEN_KEY_NAME = 'token';
  
  private readonly REFRESH_TOKEN_KEY_NAME = "refreshToken";
  
  private isAdminSubject = new BehaviorSubject<boolean>(false);
  
  isAdmin$ = this.isAdminSubject.asObservable();

  constructor(private httpClient: HttpClient) { }
  
  public saveAuthToken(value: any): void{
    localStorage.setItem(this.TOKEN_KEY_NAME, value.token);  
    localStorage.setItem(this.REFRESH_TOKEN_KEY_NAME, value.refreshToken);
  }
  
  public saveJwtToken(tokenToSave: any){
    localStorage.setItem(this.TOKEN_KEY_NAME, tokenToSave.token);
  }
  
  public isUserAuthenticated(): boolean {
    return localStorage.getItem(this.TOKEN_KEY_NAME) != null && localStorage.getItem(this.REFRESH_TOKEN_KEY_NAME) != null
  }

  public updateToken(): Observable<Object>{
    return this.httpClient.post(AppConts.BASE_URL + "/api/auth/refresh", {refreshToken: this.getRefreshToken()});
  }

  public IsUserAdmin(): Observable<boolean> {
    return this.httpClient.get<boolean>(AppConts.BASE_URL + '/is-admin').pipe(
      tap((response: any) => this.isAdminSubject.next(response.isAdmin)
    )
    );
  }

  public getCurrentIsAdmin(): boolean | null {
    return this.isAdminSubject.value;
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
