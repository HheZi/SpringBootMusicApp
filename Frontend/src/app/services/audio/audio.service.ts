import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Track } from '../../home/see-tracks/track';

@Injectable({
  providedIn: 'root'
})
export class AudioService {

  private currentTrackSubject = new BehaviorSubject<Track | null>(null);
  public currentTrack$ = this.currentTrackSubject.asObservable();

  constructor() {}

  public setCurrentTrack(track: Track): void {
    this.currentTrackSubject.next(track);
  }

}
