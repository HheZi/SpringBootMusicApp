import { Component, OnDestroy, OnInit } from '@angular/core';
import { Playlist } from './playlist';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-playlist-list',
  templateUrl: './playlist-list.component.html',
  styleUrl: './playlist-list.component.css'
})
export class PlaylistListComponent {

  protected playlists: Playlist[] = [];

  protected playlistNotFound = false;

  constructor(
    private router: Router 
  ){

  }
  ngOnDestroy(): void {
    this.playlists = []
  }

  public setPlaylists(playlists: any[]): void{
    this.playlists = playlists; 
  }

  public makePlaylistEmpty(){
    this.playlists = [];
    this.playlistNotFound = false;
  }

  public notFound(){
    this.playlistNotFound = true;
  }

  protected navigateToPlaylist(id: number){
    this.router.navigate(["playlist/"+id]);
  }

}
