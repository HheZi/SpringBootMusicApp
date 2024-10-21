import { HttpClient } from '@angular/common/http';
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

  public getAuthorsByIds(ids: number[]): Observable<Object>{
    return this.httpClient.get(this.AUTHOR_URL, {headers: this.authService.getAuthTokenInHeader(), params: {ids: ids}, responseType: 'json'})
  }
}
