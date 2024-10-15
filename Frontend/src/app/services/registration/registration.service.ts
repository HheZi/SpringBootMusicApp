import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppConts } from '../../app.consts';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  private registrationUrl = AppConts.ORIGIN_URL + '/api/users/';

  constructor(private httpClient: HttpClient) {}

  public registerUser(username: string, email: string, password: string): Observable<Object>{
    return this.httpClient.post(this.registrationUrl, {username: username, email: email, password: password})
  }
}
