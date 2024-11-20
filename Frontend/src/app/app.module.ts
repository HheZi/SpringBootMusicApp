import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { LoginComponent } from './login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule, provideHttpClient } from '@angular/common/http';
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
import { CreateAlbumComponent } from './home/create-album/create-album.component';
import { FileUploadModule } from 'primeng/fileupload';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { CascadeSelectModule } from 'primeng/cascadeselect';
import { DropdownModule } from 'primeng/dropdown';
import { CreateTrackComponent } from './home/create-track/create-track.component';
import { HeaderComponent } from './home/header/header.component';
import { MenubarModule } from 'primeng/menubar';
import { RadioButtonModule } from 'primeng/radiobutton';
import { CreateAuthorComponent } from './home/create-author/create-author.component';
import { MenuModule } from 'primeng/menu';
import { AuthorizationInterceptorService } from './interceptor/authorization-interceptor.service';
import { SeeAlbumComponent } from './home/see-album/see-album.component';
import { DialogModule } from 'primeng/dialog';
import { CalendarModule } from 'primeng/calendar';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { SeeAuthorComponent } from './home/see-author/see-author.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegistrationComponent,
    HomeComponent,
    AudioComponent,
    SeeTracksComponent,
    CreateAlbumComponent,
    CreateTrackComponent,
    HeaderComponent,
    CreateAuthorComponent,
    SeeAlbumComponent,
    SeeAuthorComponent
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
