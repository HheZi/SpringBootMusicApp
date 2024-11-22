import { NgModule } from '@angular/core';
import { RouteReuseStrategy, RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { HomeComponent } from './home/home.component';
import { SeeTracksComponent } from './home/home-page/home-page.component';
import { CreateAlbumComponent } from './home/create-album/create-album.component';
import { CustomReuseStrategy } from './custome-routes/custom-reuse-strategy';
import { CreateTrackComponent } from './home/create-track/create-track.component';
import { CreateAuthorComponent } from './home/create-author/create-author.component';
import { SeeAlbumComponent } from './home/see-album/see-album.component';
import { SeeAuthorComponent } from './home/see-author/see-author.component';
import { CreatePlaylistComponent } from './home/create-playlist/create-playlist.component';

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
  component: HomeComponent,
  children: [
  {path: '', component: SeeTracksComponent}, 
  {path: 'albums/create', component: CreateAlbumComponent},
  {path: 'tracks/create', component: CreateTrackComponent},
  {path: 'authors/create', component: CreateAuthorComponent},
  {path: 'album/:id', component: SeeAlbumComponent},
  {path: 'author/:id', component: SeeAuthorComponent},
  {path: 'playlist/create', component: CreatePlaylistComponent}
]
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [{provide: RouteReuseStrategy, useClass: CustomReuseStrategy}]
})
export class AppRoutingModule { }
