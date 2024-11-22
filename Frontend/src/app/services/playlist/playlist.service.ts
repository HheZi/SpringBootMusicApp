import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppConts } from '../../app.consts';

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {

  private readonly PLAYLIST_URL: string = AppConts.BASE_URL + "/api/playlists/"

  constructor(
    private httpClient: HttpClient
  ) { }

  public createPlaylist(body: FormData):  Observable<Object>{
    return this.httpClient.post(this.PLAYLIST_URL, body);
  }

}
