import { Component, ElementRef, numberAttribute, OnDestroy, OnInit, ViewChild } from '@angular/core';
import Plyr from 'plyr';
import { AudioService } from '../../services/audio/audio.service';
import { TracksToPlay } from '../../services/audio/tracks-to-play';
import { Router } from '@angular/router';

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
  constructor(private audioService: AudioService, private router: Router) { }

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
      this.playNextTrack(false);
    })
  }

  public playAudio(): void {
    if (!this.tracksToPlay) return

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
        album: track.albumName + "",
        artwork: [{ src: track.imageUrl, type: "mpeg" }]
      });
      this.plyr.play();
    }
  }

  private playNextRandomTrack(): void {
    if (this.indexOfExcludedIndices < this.excludedIndices.length - 1) {
      this.indexOfExcludedIndices++;
      this.tracksToPlay.indexOfCurrentTrack = this.excludedIndices[this.indexOfExcludedIndices];
      this.playAudio();
    } else {
      this.isStopped = true;
      this.makeAllTracksIsNotPlayingProperty();
    }
  }

  private playPreviousRandomTrack(): void {
    if (this.indexOfExcludedIndices > 0) {
      this.indexOfExcludedIndices--;
      this.tracksToPlay.indexOfCurrentTrack = this.excludedIndices[this.indexOfExcludedIndices];
      this.playAudio();
    }
  }

  public makeRandomPlayTracks(): void {
    if (!this.tracksToPlay) return;

    this.isRandom = !this.isRandom;
    if (this.isRandom) {
      this.generateRandomIndices();
      this.indexOfExcludedIndices = 0;
    } else {
      this.excludedIndices = [];
    }

  }

  private generateRandomIndices(): void {
    const max = this.tracksToPlay.tracks.length;
    const currentTrackIndex = this.tracksToPlay.indexOfCurrentTrack;


    this.excludedIndices = Array.from({ length: max }, (_, i) => i)
      .filter(index => index !== currentTrackIndex)
      .sort(() => Math.random() - 0.5);


    this.excludedIndices.unshift(currentTrackIndex);


    this.indexOfExcludedIndices = 0;
  }


  public playOrStopTrack(): void {
    if (!this.tracksToPlay) return;

    this.isStopped = !this.isStopped;
    var val = this.tracksToPlay.tracks[this.tracksToPlay.indexOfCurrentTrack].isNowPlaying;
    this.tracksToPlay.tracks[this.tracksToPlay.indexOfCurrentTrack].isNowPlaying = !val;
    if (this.isStopped) {
      this.plyr.pause();
    }
    else {
      this.plyr.play();
    }
  }

  public playNextTrack(buttonPressed: boolean) {
    if (!this.tracksToPlay)
      return;

    if (this.isRepeated && !buttonPressed) {
      this.playAudio();
    }
    else if (this.isRandom) {
      this.playNextRandomTrack();
    }
    else if (this.tracksToPlay.indexOfCurrentTrack < this.tracksToPlay.tracks.length - 1) {
      this.updateTracksToPlayAndPlayTrack(1);
    }
    else if (!buttonPressed) {
      this.makeAllTracksIsNotPlayingProperty();
      this.isStopped = true;
    }
  }

  public playPreviousTrack() {
    if (!this.tracksToPlay) return;

    if (this.isRandom) {
      this.playPreviousRandomTrack();
    }
    else if (this.tracksToPlay.indexOfCurrentTrack > 0) {
      this.updateTracksToPlayAndPlayTrack(-1);
    }
    else
      this.playAudio();
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

  protected seeAlbumOfTrack() {
    if(this.tracksToPlay){
      this.router.navigate(["album/"+ this.tracksToPlay.tracks[this.tracksToPlay.indexOfCurrentTrack].albumId]);
    }
  }

  protected seeAuthorOfTrack(){
    if(this.tracksToPlay){
      this.router.navigate(["author/"+ this.tracksToPlay.tracks[this.tracksToPlay.indexOfCurrentTrack].authorId]);
    }
  }

  ngOnDestroy(): void {
    if (this.plyr)
      this.plyr.destroy()
  }
}
