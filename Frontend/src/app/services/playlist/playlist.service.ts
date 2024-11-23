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

  public getTracksBySymbol(symbol: string): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + "symbol/" + symbol);
  }

  public createPlaylist(body: FormData): Observable<Object>{
    return this.httpClient.post(this.PLAYLIST_URL, body);
  }

  public getPlaylist(id: number): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + id);
  }

  public getIsOwnerOfPlaylist(id: number): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + id +"/owner");
  }

  public savePlaylist(formData: FormData, id: number): Observable<Object>{
    return this.httpClient.put(this.PLAYLIST_URL + id, formData);
  }

}
