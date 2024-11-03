import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppConts } from '../../app.consts';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private loginUrl: string = AppConts.BASE_URL + '/api/auth/login';

  constructor(private httpClient: HttpClient) { }

  public loginUser(username: string, password: string): Observable<Object> {
    return this.httpClient.post(this.loginUrl, { username: username, password: password }, { responseType: "json" });

  }

}
