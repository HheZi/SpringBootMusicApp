import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AudioComponent } from "./audio/audio.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, AudioComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'front-end';
}
