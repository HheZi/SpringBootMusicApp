import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {  Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private originUrl = 'http://localhost:8080';

  private loginUrl: string = this.originUrl + '/login';


  constructor(private httpClient: HttpClient)  { }

  public loginUser(username: string, password: string): Observable<string> {
    return this.httpClient.post(this.loginUrl, { username: username, password: password }, {responseType: "text",});

  }
  
}
