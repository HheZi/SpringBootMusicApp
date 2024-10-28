import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { AppConts } from '../../app.consts';
import { Observable } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { TrackService } from '../track/track.service';

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {

  private readonly PLAYLIST_URL: string = AppConts.BASE_URL + "/api/playlists/";

  constructor(private httpClient: HttpClient, private authService: AuthService, private trackService: TrackService) {}

  public getPlaylists(id: number): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + id, {headers: this.authService.getAuthTokenInHeader(), responseType: "json"})
  }

  public getPlaylistsBySymbol(symbol: string): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + "symbol/" + symbol, {headers: this.authService.getAuthTokenInHeader()});
  }

  public getPlaylistTypes(): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + "types", {headers: this.authService.getAuthTokenInHeader(), responseType: "json"});
  }

  public createPlaylist(formData: FormData): Observable<Object>{
    return this.httpClient.post(this.PLAYLIST_URL, formData, {headers: this.authService.getAuthTokenInHeader()});
  }  
}
