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
    return this.httpClient.get(this.AUTHOR_URL + id, { responseType: 'json'});
  }

  public getAuthorsBySymbol(symbol: string): Observable<Object>{
    return this.httpClient.get(this.AUTHOR_URL + "symbol/" + symbol, { responseType: "json"});
  }

  public createAuthor(body: FormData): Observable<Object>{
    return this.httpClient.post(this.AUTHOR_URL, body, { responseType: "json"});
  }

  public canModify(id: number): Observable<Object>{
    return this.httpClient.get(this.AUTHOR_URL + "owner/" + id);
  }

  public updateAuthor(data: FormData, id: number){
    return this.httpClient.put(this.AUTHOR_URL + id, data);
  }

}
