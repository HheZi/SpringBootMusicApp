import { NgModule } from '@angular/core';
import { RouteReuseStrategy, RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { HomeComponent } from './home/home.component';
import { SeeTracksComponent } from './home/see-tracks/see-tracks.component';
import { CreatePlaylistComponent } from './home/create-playlist/create-playlist.component';
import { CustomReuseStrategy } from './custome-routes/custom-reuse-strategy';
import { CreateTrackComponent } from './home/create-track/create-track.component';
import { CreateAuthorComponent } from './home/create-author/create-author.component';
import { SeePlaylistComponent } from './home/see-playlist/see-playlist.component';
import { SeeAuthorComponent } from './home/see-author/see-author.component';

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
  {path: 'tracks/see', component: SeeTracksComponent}, 
  {path: 'playlists/create', component: CreatePlaylistComponent},
  {path: 'tracks/create', component: CreateTrackComponent},
  {path: 'authors/create', component: CreateAuthorComponent},
  {path: 'playlist/see/:id', component: SeePlaylistComponent},
  {path: 'author/see/:id', component: SeeAuthorComponent}
]
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [{provide: RouteReuseStrategy, useClass: CustomReuseStrategy}]
})
export class AppRoutingModule { }
