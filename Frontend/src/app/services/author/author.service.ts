import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { AppConts } from '../../app.consts';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthorService {

  private readonly AUTHOR_URL = AppConts.BASE_URL + '/api/authors/';

  constructor(private httpClient: HttpClient, private authService: AuthService) { }

  public getAuthorsById(id: number): Observable<Object>{
    return this.httpClient.get(this.AUTHOR_URL + id, {headers: this.authService.getAuthTokenInHeader(), responseType: 'json'});
  }

  public getAuthorsBySymbol(symbol: string): Observable<Object>{
    return this.httpClient.get(this.AUTHOR_URL + "symbol/" + symbol, {headers: this.authService.getAuthTokenInHeader(), responseType: "json"});
  }

  public createAuthor(body: any): Observable<Object>{
    return this.httpClient.post(this.AUTHOR_URL, body, {headers: this.authService.getAuthTokenInHeader(), responseType: "json"});
  }

}
