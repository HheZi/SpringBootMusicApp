import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent implements OnInit{

  constructor(
    private authService: AuthService
  ){}

  ngOnInit(): void {
    this.authService.IsUserAdmin().subscribe()
  }
  

}
