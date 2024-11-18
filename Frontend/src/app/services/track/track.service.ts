import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppConts } from '../../app.consts';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TrackService {
  
  private readonly TRACK_URL_TO_GET_FROM_AGGREGATION = AppConts.BASE_URL + '/tracks';
  
  private readonly TRACK_URL = AppConts.BASE_URL + "/api/tracks/"
  
  constructor(private httpClient: HttpClient, private authService: AuthService) { }
  
  public getTracks(header: any) : Observable<Object>{
    if(!header){
      return this.httpClient.get(this.TRACK_URL_TO_GET_FROM_AGGREGATION);
    }
    return this.httpClient.get(this.TRACK_URL_TO_GET_FROM_AGGREGATION, {params: header});
  }
  
  public getTracksByAuthorId(id: any) : Observable<Object>{
    return this.httpClient.get(this.TRACK_URL_TO_GET_FROM_AGGREGATION, {params: {authorId: id}});
  }

  public getTracksByPlaylistId(id: any) : Observable<Object> {
    return this.httpClient.get(this.TRACK_URL_TO_GET_FROM_AGGREGATION, {params: {playlistId: id}});
  }

  public createTracks(tracks: any): Observable<Object>{
    return this.httpClient.post(this.TRACK_URL, tracks);
  }

  public deleteTrack(id: number): Observable<Object>{
    return this.httpClient.delete(this.TRACK_URL + id);
  }

  public getTrackInPlaylist(playlistId: number): Observable<Object>{
    return this.httpClient.get(this.TRACK_URL + "count/"+playlistId);
  }

  public updateTrackTitle(id: number, title: string): Observable<Object>{
    return this.httpClient.patch(this.TRACK_URL + id, {"title": title});
  }
}
