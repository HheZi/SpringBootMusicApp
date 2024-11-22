import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Track } from '../../home/home-page/track';
import { TracksToPlay } from './tracks-to-play';

@Injectable({
  providedIn: 'root'
})
export class AudioService {

  private TracksToPlaySubject = new BehaviorSubject<TracksToPlay | null>(null);
  public TracksToPlay$ = this.TracksToPlaySubject.asObservable();


  constructor() {}

  public setTracks(tracks: Track[], currentTrackIndex: number): void {
    this.TracksToPlaySubject.next({"tracks": tracks, "indexOfCurrentTrack": currentTrackIndex});
  }
  
}
