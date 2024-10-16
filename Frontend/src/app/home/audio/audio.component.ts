import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import Plyr from 'plyr';

@Component({
  selector: 'app-audio',
  templateUrl: './audio.component.html',
  styleUrl: './audio.component.css'
})
export class AudioComponent implements OnInit, OnDestroy {
  @ViewChild('audioPlayer', { static: true })
  private audioPlayerRef!: ElementRef;
  private plyr!: Plyr;
  public src: string = 'http://localhost:8080/api/audio/master_of_puppets';

  ngOnInit(): void {
    this.plyr = new Plyr(this.audioPlayerRef.nativeElement, { controls: ['play', 'progress', 'current-time', 'mute', 'volume'], })
  }
  ngOnDestroy(): void {
    this.plyr.source
    if(this.plyr)
      this.plyr.destroy()
  }
}
