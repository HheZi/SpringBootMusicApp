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
  ){}

  public getPlaylistBySymbol(symbol: string): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + "symbol/" + symbol);
  }

  public createPlaylist(body: FormData): Observable<Object>{
    return this.httpClient.post(this.PLAYLIST_URL, body);
  }

  public getPlaylistsByOwner(): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + '/users/mine/');
  }

  public getPlaylist(id: number): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + id);
  }

  public getIsOwnerOfPlaylist(id: number): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + "owner/" + id );
  }

  public savePlaylist(formData: FormData, id: number): Observable<Object>{
    return this.httpClient.put(this.PLAYLIST_URL + id, formData);
  }

  public addTrackToPlaylist(playlistId:number, trackId: number): Observable<Object>{
    return this.httpClient.patch(this.PLAYLIST_URL + playlistId+"/"+trackId, null);
  }

  public deleteTrackFromPlaylist(playlistId:number, trackId: number): Observable<Object>{
    return this.httpClient.delete(this.PLAYLIST_URL + playlistId+"/"+trackId);
  }

  public deleteCover(id: number): Observable<Object>{
    return this.httpClient.delete(this.PLAYLIST_URL + "cover/" + id  );
  }

  public deletePlaylist(id: number){
    return this.httpClient.delete(this.PLAYLIST_URL + id);
  }
}
