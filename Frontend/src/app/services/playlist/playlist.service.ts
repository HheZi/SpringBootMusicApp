import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { AppConts } from '../../app.consts';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {

  private readonly PLAYLIST_URL: string = AppConts.BASE_URL + "/api/playlists/";

  constructor(private httpClient: HttpClient, private authService: AuthService) {}

  public getPlaylists(ids: number[]){
    return this.httpClient.get(this.PLAYLIST_URL, {headers: this.authService.getAuthTokenInHeader(), params: {ids: ids}})
  }

  public getPlaylistTypes(): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + "types", {headers: this.authService.getAuthTokenInHeader(), responseType: "json"});
  }
}
