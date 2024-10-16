import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppConts } from '../../app.consts';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private loginUrl: string = AppConts.ORIGIN_URL + '/login';

  constructor(private httpClient: HttpClient) { }

  public loginUser(username: string, password: string): Observable<string> {
    return this.httpClient.post(this.loginUrl, { username: username, password: password }, { responseType: "text" });

  }

}
