import { Component } from '@angular/core';
import { MessageService } from 'primeng/api';


@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrl: './app.component.css',
    providers: [MessageService],
    standalone: false
})
export class AppComponent {
 
}
