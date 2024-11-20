import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  public items: MenuItem[] = [
    {label: "Track", routerLink: "tracks/create"},
    {label: "Albums", routerLink: "albums/create"},
    {label: "Author", routerLink: "authors/create"},
  ];
  public butLabel: string = "Create";
  
  public constructor(
    private router: Router,
    private authService: AuthService
  ){}

  public textInput: string = '';

  onSearch() {  
    this.router.navigate(['tracks'], {queryParams: {'name': this.textInput}})
  }
  
  navigateTo(arg0: string) {
    this.router.navigate([arg0])
  }

  public logout(){
    this.authService.logout();
    this.router.navigate(['login'])
  }

}
