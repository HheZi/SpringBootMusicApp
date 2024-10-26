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

  public getPlaylists(ids: number[]){
    return this.httpClient.get(this.PLAYLIST_URL, {headers: this.authService.getAuthTokenInHeader(), params: {ids: ids}})
  }

  public getPlaylistTypes(): Observable<Object>{
    return this.httpClient.get(this.PLAYLIST_URL + "types", {headers: this.authService.getAuthTokenInHeader(), responseType: "json"});
  }

  public createPlaylist(playlistForm: FormGroup){
    const formData = new FormData();
    formData.append("cover", playlistForm.get("cover")?.value);
    formData.append("name", playlistForm.get("title")?.value);
    formData.append("playlistType", playlistForm.get("playlistType")?.value);
    
    this.httpClient.post(this.PLAYLIST_URL, formData, {headers: this.authService.getAuthTokenInHeader()}).subscribe({
        next: (playlistId) => {
          playlistForm.get("tracks")?.value.forEach((track: any) => {
            track.playlistId = playlistId;
            if(playlistForm.get("playlistType")?.value === "ALBUM" || playlistForm.get("playlistType")?.value === "MINI_ALBUM"){
              track.author = playlistForm.get("author")?.value
            }
          });
          // this.trackService.createTracks(playlistForm.get("tracks")?.value)
        }
      })
  }
}
