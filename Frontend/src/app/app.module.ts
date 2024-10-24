import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { LoginComponent } from './login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { ToastModule } from 'primeng/toast';
import { MessageModule } from 'primeng/message';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { RegistrationComponent } from './registration/registration.component';
import { HomeComponent } from './home/home.component';
import { AudioComponent } from './home/audio/audio.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { SeeTracksComponent } from './home/see-tracks/see-tracks.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DataViewModule } from 'primeng/dataview';
import { CreatePlaylistComponent } from './home/create-playlist/create-playlist.component';
import { FileUploadModule } from 'primeng/fileupload';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { CascadeSelectModule } from 'primeng/cascadeselect';
import { DropdownModule } from 'primeng/dropdown';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegistrationComponent,
    HomeComponent,
    AudioComponent,
    SeeTracksComponent,
    CreatePlaylistComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    CardModule,     
    InputTextModule, 
    PasswordModule,  
    ButtonModule,    
    ToastModule,
    MessageModule,
    NoopAnimationsModule,
    NgbModule,
    DataViewModule,
    ReactiveFormsModule,
    FileUploadModule,
    AutoCompleteModule,
    DropdownModule 
  ],
  providers: [provideHttpClient()],
  bootstrap: [AppComponent]
})
export class AppModule { }
