import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private originUrl = 'http://localhost:8080';

  private loginUrl: string = this.originUrl + '/login';

  
  constructor(private httpClient: HttpClient) { }
  
  public loginUser(username: string, password: string): void {
    this.httpClient.post(this.loginUrl, { username: username, password: password },
      { headers: new HttpHeaders().set("Access-Control-Allow-Origin", this.originUrl) ,  responseType: "text"}, )
      .subscribe({
        next: (data) => {
          console.log(`response is ${data}`)
        }
      })
  }
  
}
