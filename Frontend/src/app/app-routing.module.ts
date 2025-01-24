import { NgModule } from '@angular/core';
import { RouteReuseStrategy, RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { MainComponent } from './main/main.component';
import { HomeComponent } from './main/home-page/home-page.component';
import { CreateAlbumComponent } from './main/create-album/create-album.component';
import { CustomReuseStrategy } from './custome-routes/custom-reuse-strategy';
import { CreateTrackComponent } from './main/create-track/create-track.component';
import { CreateAuthorComponent } from './main/create-author/create-author.component';
import { SeeAlbumComponent } from './main/see-album/see-album.component';
import { SeeAuthorComponent } from './main/see-author/see-author.component';
import { CreatePlaylistComponent } from './main/create-playlist/create-playlist.component';
import { SeePlaylistComponent } from './main/see-playlist/see-playlist.component';
import { SeeFavoriteTracksComponent } from './main/see-favorite-tracks/see-favorite-tracks.component';
import { authGuard } from './auth-guard.guard';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

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
  {path: 'home', component: HomeComponent}, 
  {path: 'album/create', component: CreateAlbumComponent, canActivate: [authGuard]},
  {path: 'track/create', component: CreateTrackComponent, canActivate: [authGuard]},
  {path: 'author/create', component: CreateAuthorComponent, canActivate: [authGuard]},
  {path: 'playlist/create', component: CreatePlaylistComponent, canActivate: [authGuard]},
  {path: 'album/:id', component: SeeAlbumComponent},
  {path: 'author/:id', component: SeeAuthorComponent},
  {path: 'playlist/:id', component: SeePlaylistComponent},
  {path: 'favorite/tracks', component: SeeFavoriteTracksComponent}
  ],
},
{
  path: '**',
  component: PageNotFoundComponent
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [{provide: RouteReuseStrategy, useClass: CustomReuseStrategy}]
})
export class AppRoutingModule { }
