import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppConts } from '../../app.consts';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TrackService {
  
  private readonly TRACK_URL_TO_GET_FROM_AGGREGATION = AppConts.BASE_URL + '/tracks/';
  
  private readonly TRACK_URL = AppConts.BASE_URL + "/api/tracks/"
  
  constructor(private httpClient: HttpClient, private authService: AuthService) { }
  
  public getTracks(httpParams: HttpParams | null) : Observable<Object>{
    if(httpParams){
      return this.httpClient.get(this.TRACK_URL_TO_GET_FROM_AGGREGATION, {params: httpParams});
    }
    return this.httpClient.get(this.TRACK_URL_TO_GET_FROM_AGGREGATION);
  }

  public getDurationByIds(ids: number | number[]): Observable<Object>{
    return this.httpClient.get(this.TRACK_URL + "duration", {params: {"ids": ids}});
  }

  public getTracksByTrackName(name: string, page: number): Observable<Object>{
    return this.httpClient.get(this.TRACK_URL_TO_GET_FROM_AGGREGATION, {params: {name: name, page: page}})
  }

  public getTracksByAlbumId(id: number[] | number, page: number) : Observable<Object> {
    return this.httpClient.get(this.TRACK_URL_TO_GET_FROM_AGGREGATION, {params: {albumId: id, page: page}});
  }

  public getTrackByIds(ids: number[], page: number){    
    return this.httpClient.get(this.TRACK_URL_TO_GET_FROM_AGGREGATION, {params: {"id": ids, page: page}});
  }

  public createTrack(tracks: any): Observable<Object>{
    return this.httpClient.post(this.TRACK_URL, tracks);
  }

  public deleteTrack(id: number): Observable<Object>{
    return this.httpClient.delete(this.TRACK_URL + id);
  }

  public getTrackInAlbum(albumId: number): Observable<Object>{
    return this.httpClient.get(this.TRACK_URL + "count/"+albumId);
  }

  public updateTrackTitle(id: number, title: string): Observable<Object>{
    return this.httpClient.patch(this.TRACK_URL + id, {"title": title});
  }
}
