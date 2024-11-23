import { Component, OnInit, ViewChild } from '@angular/core';
import { Playlist } from './playlist';
import { Title } from '@angular/platform-browser';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { ActivatedRoute } from '@angular/router';
import { TrackListComponent } from '../track-list/track-list.component';
import { MessageService } from 'primeng/api';
import { TrackService } from '../../services/track/track.service';
import { HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-see-playlist',
  templateUrl: './see-playlist.component.html',
  styleUrl: './see-playlist.component.css'
})
export class SeePlaylistComponent implements OnInit{
  protected playlist: Playlist = {id: 0, name: "", imageUrl: "", description: "", trackIds: []};
  protected isNotFound: boolean = false;

  @ViewChild(TrackListComponent) trackList!: TrackListComponent;

  constructor(
    private title: Title,
    private playlistService: PlaylistService,
    private trackService: TrackService,
    private activetedRoute: ActivatedRoute,
    private messageService: MessageService
  ){}

  ngOnInit(): void {
    const albumId = this.activetedRoute.snapshot.paramMap.get('id');
    if(albumId)
      this.loadPlaylist(parseInt(albumId))
  }

  private loadPlaylist(id: number){
    this.playlistService.getPlaylist(id).subscribe({
      next: (playlist: any) => {
        this.title.setTitle(playlist.name);
        this.playlist = playlist;
        
        if(this.playlist.trackIds	){
          this.trackService.getTrackByIds(this.playlist.trackIds).subscribe({
            next: (tracksResp: any) => this.trackList.setTracks(tracksResp) 
          })
        }
        
      },
      error: () => this.isNotFound = true
    })

  }
}
