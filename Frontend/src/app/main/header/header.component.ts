import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit{
  public items: MenuItem[] = [
    {label: "Create Track", routerLink: "track/create"},
    {label: "Create Album", routerLink: "album/create"},
    {label: "Create Playlist", routerLink: "playlist/create"},
    {label: "Create Author", routerLink: "author/create"}
  ];
  public butLabel: string = "Create";

  protected isAdmin: boolean = false;
  
  public constructor(
    private router: Router,
    private authService: AuthService
  ){}

  ngOnInit(): void {
    this.authService.isAdmin$.subscribe((val: boolean) => {
        this.isAdmin = val;
    });
  }

  public textInput: string = '';

  onSearch() {  
    if (this.textInput){
      this.router.navigate(['/home'], {queryParams: {'name': this.textInput}});
    }
    else{
      this.router.navigate(['/home']);
    }
  }
  
  navigateTo(arg0: string) {
    this.textInput = '';
    this.router.navigate([arg0])
  }

  public logout(){
    this.authService.logout();
    this.router.navigate(['login'])
  }

}
