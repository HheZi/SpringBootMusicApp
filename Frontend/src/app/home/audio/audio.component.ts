import { Component, ElementRef, numberAttribute, OnDestroy, OnInit, ViewChild } from '@angular/core';
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
  public isStopped: boolean = true;
  public isRepeated: boolean = false;
  public isRandom: boolean = false;

  private tracksToPlay!: TracksToPlay;
  private excludedIndices: number[] = [];
  private indexOfExcludedIndices: number = 0;
  constructor(private audioService: AudioService) { }

  ngOnInit(): void {
    this.plyr = new Plyr(this.audioPlayerRef.nativeElement, { controls: ['progress', 'current-time', 'mute', 'volume'] });
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
    this.isStopped = false

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
        artwork: [{ src: track.imageUrl, type: "mpeg" }]
      });
      this.plyr.play();
    }
  }

  private playNextRandomTrack(): void {
    if (this.excludedIndices.length >= this.tracksToPlay.tracks.length) {
      this.isStopped = true;
      this.makeAllTracksIsNotPlayingProperty();
      return;
    }

    let randomIndex;
    do {
      randomIndex = Math.floor(Math.random() * this.tracksToPlay.tracks.length );
    } while (this.excludedIndices.includes(randomIndex));

    this.excludedIndices.push(randomIndex);
    this.indexOfExcludedIndices++;
    this.tracksToPlay.indexOfCurrentTrack = randomIndex;
    this.playAudio();
  }

  private playPreviousRandomTrack(): void{
    if(this.indexOfExcludedIndices > 0){
      this.indexOfExcludedIndices--;
      this.tracksToPlay.indexOfCurrentTrack = this.indexOfExcludedIndices;
      this.playAudio();
    }
  }

  public makeRandomPlayTracks(): void {
    if (!this.tracksToPlay) return;

    this.excludedIndices = [this.tracksToPlay.indexOfCurrentTrack];
    let randomIndex;
    do {
      randomIndex = Math.floor(Math.random() * this.tracksToPlay.tracks.length );
    } while (this.excludedIndices.includes(randomIndex) && this.excludedIndices.length != this.tracksToPlay.tracks.length);

    this.isRandom = !this.isRandom;
  }

  public playOrStopTrack(): void {
    if (!this.tracksToPlay) return;

    this.isStopped = !this.isStopped;
    if (this.isStopped) {
      this.plyr.pause();
    }
    else {
      this.plyr.play();
    }
  }

  public playNextTrack() {
    if (!this.tracksToPlay)
      return;

    if (this.isRepeated) {
      this.playAudio();
    }
    else if (this.isRandom) {
      this.playNextRandomTrack();
    }
    else if (this.tracksToPlay.indexOfCurrentTrack < this.tracksToPlay.tracks.length - 1) {
      this.updateTracksToPlayAndPlayTrack(1);
    }
    else {
      this.makeAllTracksIsNotPlayingProperty();
      this.isStopped = true;
    }
  }

  public playPreviousTrack() {
    if (!this.tracksToPlay) return;

    if (this.isRepeated) {
      this.playAudio();
    }
    else if(this.isRandom){
      this.playPreviousRandomTrack();
    }
    else if (this.tracksToPlay.indexOfCurrentTrack > 0) {
      this.updateTracksToPlayAndPlayTrack(-1);
    }
  }

  private updateTracksToPlayAndPlayTrack(value: number) {
    this.tracksToPlay.indexOfCurrentTrack += value;
    this.playAudio();
  }

  private makeAllTracksIsNotPlayingProperty() {
    for (let index = 0; index < this.tracksToPlay.tracks.length; index++) {
      this.tracksToPlay.tracks[index].isNowPlaying = false;
    }
  }


  ngOnDestroy(): void {
    if (this.plyr)
      this.plyr.destroy()
  }
}
