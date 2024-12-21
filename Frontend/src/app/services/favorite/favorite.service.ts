import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppConts } from '../../app.consts';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {

  private readonly FAVORITES_TRACKS_URL = AppConts.BASE_URL + "/api/favorites/tracks/"

  constructor(
    private httpClient: HttpClient
  ) { }

  public addTrackToFavorites(trackId: number): Observable<Object>{
    return this.httpClient.post(this.FAVORITES_TRACKS_URL + trackId, null);
  }

  public deleteTrackFromFavorites(trackId: number){
    return this.httpClient.delete(this.FAVORITES_TRACKS_URL + trackId);
  }

}
