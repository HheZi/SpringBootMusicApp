import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import Plyr from 'plyr';
import { Track } from '../see-tracks/track';
import { Subscription } from 'rxjs';
import { AudioService } from '../../services/audio/audio.service';

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

  constructor(private audioService: AudioService){}

  ngOnInit(): void { 
    this.plyr = new Plyr(this.audioPlayerRef.nativeElement, { controls: ['play', 'progress', 'current-time', 'mute', 'volume'] });
    this.audioService.currentTrack$.subscribe({
      next: (track: any) => this.playAudio(track)
    })
  }

  public playAudio(track: Track): void {
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
        artwork: [{src: track.imageUrl}]
      });
      this.plyr.play();
    }
  }

  ngOnDestroy(): void {
    if (this.plyr)
      this.plyr.destroy()
  }
}
