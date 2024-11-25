import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { LoginComponent } from './login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { ToastModule } from 'primeng/toast';
import { MessageModule } from 'primeng/message';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { RegistrationComponent } from './registration/registration.component';
import { MainComponent } from './main/main.component';
import { AudioComponent } from './main/audio/audio.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { HomeComponent } from './main/home-page/home-page.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DataViewModule } from 'primeng/dataview';
import { CreateAlbumComponent } from './main/create-album/create-album.component';
import { FileUploadModule } from 'primeng/fileupload';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { DropdownModule } from 'primeng/dropdown';
import { CreateTrackComponent } from './main/create-track/create-track.component';
import { HeaderComponent } from './main/header/header.component';
import { MenubarModule } from 'primeng/menubar';
import { RadioButtonModule } from 'primeng/radiobutton';
import { CreateAuthorComponent } from './main/create-author/create-author.component';
import { MenuModule } from 'primeng/menu';
import { AuthorizationInterceptorService } from './interceptor/authorization-interceptor.service';
import { SeeAlbumComponent } from './main/see-album/see-album.component';
import { DialogModule } from 'primeng/dialog';
import { CalendarModule } from 'primeng/calendar';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { SeeAuthorComponent } from './main/see-author/see-author.component';
import { CreatePlaylistComponent } from './main/create-playlist/create-playlist.component';
import { PlaylistListComponent } from './main/playlist-list/playlist-list.component';
import { TrackListComponent } from './main/track-list/track-list.component';
import { SeePlaylistComponent } from './main/see-playlist/see-playlist.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegistrationComponent,
    MainComponent,
    AudioComponent,
    HomeComponent,
    CreateAlbumComponent,
    CreateTrackComponent,
    HeaderComponent,
    CreateAuthorComponent,
    SeeAlbumComponent,
    SeeAuthorComponent,
    CreatePlaylistComponent,
    PlaylistListComponent,
    TrackListComponent,
    SeePlaylistComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
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
    DropdownModule,
    MenubarModule,
    RadioButtonModule,
    MenuModule,
    DialogModule,
    CalendarModule,
    ConfirmDialogModule 
  ],
  providers: [ {provide: HTTP_INTERCEPTORS, useClass: AuthorizationInterceptorService, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
