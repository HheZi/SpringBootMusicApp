import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import Plyr from 'plyr';
import { Track } from '../see-tracks/track';

@Component({
  selector: 'app-audio',
  templateUrl: './audio.component.html',
  styleUrl: './audio.component.css'
})
export class AudioComponent implements OnInit, OnDestroy {

  @ViewChild('audioPlayer', { static: true })
  private audioPlayerRef!: ElementRef;
  private static plyr: Plyr;

  ngOnInit(): void {
    if (!AudioComponent.plyr) 
      AudioComponent.plyr = new Plyr(this.audioPlayerRef.nativeElement, { controls: ['play', 'progress', 'current-time', 'mute', 'volume'] });
    
  }

  public static playAudio(track: Track): void {
    AudioComponent.plyr.source = {
      type: 'audio',
      title: track.title,
      sources: [{
        src: track.audioUrl,
        type: "audio/mpeg"
      }]
    }
    if ('mediaSession' in navigator) {
      navigator.mediaSession.metadata = new MediaMetadata({
        title: track.title,
        artist: track.author + "",
        album: track.playlist + "",
        artwork: [{src: track.imageUrl}]
      });
      AudioComponent.plyr.play();
    }
  }

  ngOnDestroy(): void {
    if (AudioComponent.plyr)
      AudioComponent.plyr.destroy()
  }
}
