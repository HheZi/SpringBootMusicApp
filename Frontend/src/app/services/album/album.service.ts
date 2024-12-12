import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { AppConts } from '../../app.consts';
import { Observable } from 'rxjs';
import { TrackService } from '../track/track.service';


@Injectable({
  providedIn: 'root'
})
export class AlbumService {
 
  private readonly ALBUM_AGGREGATION_URL = AppConts.BASE_URL + "/albums/";

  private readonly ALBUM_URL: string = AppConts.BASE_URL + "/api/albums/";
  
  constructor(private httpClient: HttpClient, private authService: AuthService, private trackService: TrackService) {}


  public getFullAlbum(id: number): Observable<Object>{
    return this.httpClient.get(this.ALBUM_AGGREGATION_URL + id);
  }
  
  public getAlbumsByIds(ids: number[]): Observable<Object>{
    const params = { ids: ids.map(id => id.toString()) };
    return this.httpClient.get(this.ALBUM_URL, { params, responseType: "json" });
  }
  
  public getAlbumsByAuthorId(authorId: number | number[]) {
    return this.httpClient.get(this.ALBUM_URL, { params: {'authorId': authorId}, responseType: "json" });
  }

  public getAlbumsBySymbol(symbol: string): Observable<Object>{
    return this.httpClient.get(this.ALBUM_URL + "symbol/" + symbol);
  }
  
  public getIsUserIsOwnerOfAlbum(albumId: number): Observable<Object>{
    return this.httpClient.get(this.ALBUM_URL + "owner/" + albumId);
  }

  public createAlbum(formData: FormData): Observable<Object>{
    return this.httpClient.post(this.ALBUM_URL, formData, );
  }  

  public updateAlbum(id: number, editAlbum: FormData) {
    return this.httpClient.put(this.ALBUM_URL + id, editAlbum);
  }

  public deleteAlbum(id: number){
    return this.httpClient.delete(this.ALBUM_URL + id);
  }

  public deleteCover(id: number){
    return this.httpClient.delete(this.ALBUM_URL + "cover/" + id);
  }
}

