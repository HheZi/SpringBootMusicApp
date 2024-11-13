import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  public items: MenuItem[] = [
    {label: "Track", routerLink: "tracks/create"},
    {label: "Playlist", routerLink: "playlists/create"},
    {label: "Author", routerLink: "authors/create"},
  ];
  public butLabel: string = "Create";
  
  public constructor(private router: Router){}

  public textInput: string = '';

  onSearch() {  
    this.router.navigate(['tracks/see'], {queryParams: {'name': this.textInput}})
  }
  
  navigateTo(arg0: string) {
    this.router.navigate([arg0])
  }

}
