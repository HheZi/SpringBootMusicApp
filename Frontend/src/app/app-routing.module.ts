import { NgModule } from '@angular/core';
import { RouteReuseStrategy, RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { MainComponent } from './main/main.component';
import { SeeTracksComponent } from './main/home-page/home-page.component';
import { CreateAlbumComponent } from './main/create-album/create-album.component';
import { CustomReuseStrategy } from './custome-routes/custom-reuse-strategy';
import { CreateTrackComponent } from './main/create-track/create-track.component';
import { CreateAuthorComponent } from './main/create-author/create-author.component';
import { SeeAlbumComponent } from './main/see-album/see-album.component';
import { SeeAuthorComponent } from './main/see-author/see-author.component';
import { CreatePlaylistComponent } from './main/create-playlist/create-playlist.component';
import { SeePlaylistComponent } from './main/see-playlist/see-playlist.component';

const routes: Routes = [
{
  path: 'login',
  component: LoginComponent
},
{
  path: "registration",
  component: RegistrationComponent
},
{
  path: '', 
  component: MainComponent,
  children: [
  {path: '', component: SeeTracksComponent}, 
  {path: 'albums/create', component: CreateAlbumComponent},
  {path: 'tracks/create', component: CreateTrackComponent},
  {path: 'authors/create', component: CreateAuthorComponent},
  {path: 'album/:id', component: SeeAlbumComponent},
  {path: 'author/:id', component: SeeAuthorComponent},
  {path: 'playlist/create', component: CreatePlaylistComponent},
  {path: 'playlist/:id', component: SeePlaylistComponent}
]
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [{provide: RouteReuseStrategy, useClass: CustomReuseStrategy}]
})
export class AppRoutingModule { }
