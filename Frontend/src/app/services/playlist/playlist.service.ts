import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { AppConts } from '../../app.consts';
import { Observable } from 'rxjs';
import { TrackService } from '../track/track.service';
import { Playlist } from '../../home/see-playlist/playlist';

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {
  private readonly PLAYLIST_URL: string = AppConts.BASE_URL + "/api/playlists/";
  
  constructor(private httpClient: HttpClient, private authService: AuthService, private trackService: TrackService) {}
  
  public getPlaylistsById(id: number): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + id, { responseType: "json"})
  }
  
  public getPlaylistsBySymbol(symbol: string): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + "symbol/" + symbol);
  }
  
  public getIsUserIsOwnerOfPlaylist(playlistId: number){
    return this.httpClient.get(this.PLAYLIST_URL + "owner/" + playlistId);
  }
  
  public getPlaylistTypes(): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + "types",  {responseType: "json"});
  }
  
  public createPlaylist(formData: FormData): Observable<Object>{
    return this.httpClient.post(this.PLAYLIST_URL, formData, );
  }  

  updatePlaylist(id: number, editPlaylist: FormData) {
    return this.httpClient.put(this.PLAYLIST_URL + id, editPlaylist);
  }

  public deletePlaylist(id: number){
    return this.httpClient.delete(this.PLAYLIST_URL + id);
  }

  public deleteCover(id: number){
    return this.httpClient.patch(this.PLAYLIST_URL + id, null);
  }
}

