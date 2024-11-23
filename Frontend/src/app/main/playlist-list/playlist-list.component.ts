import { Component, OnInit } from '@angular/core';
import { Playlist } from './playlist';
import { PlaylistService } from '../../services/playlist/playlist.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-playlist-list',
  templateUrl: './playlist-list.component.html',
  styleUrl: './playlist-list.component.css'
})
export class PlaylistListComponent implements OnInit{

  protected playlists: Playlist[] = [];

  constructor(
    private router: Router 
  ){

  }
  ngOnInit(): void {
  }

  public setPlaylists(playlists: any[]): void{
    this.playlists = playlists; 
  }

  protected navigateToPlaylist(id: number){
    this.router.navigate(["playlist/"+id]);
  }

}
