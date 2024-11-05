import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import Plyr from 'plyr';
import { Track } from '../see-tracks/track';
import { Subscription } from 'rxjs';
import { AudioService } from '../../services/audio/audio.service';
import { TracksToPlay } from '../../services/audio/tracks-to-play';

@Component({
  selector: 'app-audio',
  templateUrl: './audio.component.html',
  styleUrl: './audio.component.css'
})
export class AudioComponent implements OnInit, OnDestroy {

  @ViewChild('audioPlayer', { static: true })
  private audioPlayerRef!: ElementRef;
  private plyr!: Plyr;
  public author: string = 'Author';
  public title: string = 'Title';
  public coverUrl: string = 'http://localhost:8080/api/images/default';
  private subscription: Subscription | null = null;

  private tracksToPlay!: TracksToPlay;

  constructor(private audioService: AudioService){}

  ngOnInit(): void { 
    this.plyr = new Plyr(this.audioPlayerRef.nativeElement, { controls: ['play', 'progress', 'current-time', 'mute', 'volume'] });
    this.audioService.TracksToPlay$.subscribe({
      next: (value) => {
        let tracksToPlay = value as TracksToPlay;
        this.tracksToPlay = tracksToPlay
        this.playAudio();
      }
    })
    this.plyr.on('ended', () => {
      this.playNextTrack();
    })
  }

  public playAudio(): void {
    let track = this.tracksToPlay.tracks[this.tracksToPlay.indexOfCurrentTrack];
    this.makeAllTracksIsNotPlayingProperty();
    track.isNowPlaying = true;

    this.plyr.source = {
      type: 'audio',
      title: track.title,
      sources: [{
        src: track.audioUrl,
        type: "audio/mpeg"
      }]
    }
    this.author = track.author;
    this.coverUrl = track.imageUrl;
    this.title = track.title;
    if ('mediaSession' in navigator) {
      navigator.mediaSession.metadata = new MediaMetadata({
        title: track.title,
        artist: track.author + "",
        album: track.playlist + "",
        artwork: [{src: track.imageUrl, type: "mpeg"}]
      });
      this.plyr.play();
    }
  }

  public playNextTrack(){
    if(this.tracksToPlay && this.tracksToPlay.indexOfCurrentTrack < this.tracksToPlay.tracks.length - 1){
      this.updateTracksToPlayAndPlayTrack(1);
    }
  }

  public playPreviousTrack(){
    if(this.tracksToPlay && this.tracksToPlay.indexOfCurrentTrack > 0){
      this.updateTracksToPlayAndPlayTrack(-1);
    }
  }

  private updateTracksToPlayAndPlayTrack(value: number){
    this.tracksToPlay.indexOfCurrentTrack += value;
    this.playAudio();
  }

  private makeAllTracksIsNotPlayingProperty(){
    for (let index = 0; index < this.tracksToPlay.tracks.length; index++) {
      this.tracksToPlay.tracks[index].isNowPlaying = false;
    }
  }

  ngOnDestroy(): void {
    if (this.plyr)
      this.plyr.destroy()
  }
}
