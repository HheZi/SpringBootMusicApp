import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppConts } from '../../app.consts';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TrackService {

  private readonly TRACK_URL = AppConts.BASE_URL + '/api/tracks/'; 

  constructor(private httpClient: HttpClient, private authService: AuthService) { }

  public getTracks() : Observable<Object>{
    return this.httpClient.get(this.TRACK_URL, {headers: this.authService.getAuthTokenInHeader()});
  }

  public createTracks(body: any): void{
    this.httpClient.post(this.TRACK_URL, body, {headers: this.authService.getAuthTokenInHeader()})
  }
}
