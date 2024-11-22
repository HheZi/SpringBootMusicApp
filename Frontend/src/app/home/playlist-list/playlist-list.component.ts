import { Component, OnInit } from '@angular/core';
import { Playlist } from './playlist';
import { PlaylistService } from '../../services/playlist/playlist.service';

@Component({
  selector: 'app-playlist-list',
  templateUrl: './playlist-list.component.html',
  styleUrl: './playlist-list.component.css'
})
export class PlaylistListComponent implements OnInit{

  public playlists: Playlist[] = [];

  constructor(
  ){

  }
  ngOnInit(): void {
  }

  public setPlaylists(playlists: any[]): void{
    this.playlists = playlists; 
  }

}
