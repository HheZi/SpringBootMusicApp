import { NgModule } from '@angular/core';
import { RouteReuseStrategy, RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { HomeComponent } from './home/home.component';
import { SeeTracksComponent } from './home/see-tracks/see-tracks.component';
import { CreatePlaylistComponent } from './home/create-playlist/create-playlist.component';
import { CustomReuseStrategy } from './custome-routes/custom-reuse-strategy';
import { CreateTrackComponent } from './home/create-track/create-track.component';

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
  children: [{path: 'tracks/see', component: SeeTracksComponent}, 
  {path: 'playlist/create', component: CreatePlaylistComponent},
  {path: 'tracks/create', component: CreateTrackComponent}]
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [{provide: RouteReuseStrategy, useClass: CustomReuseStrategy}]
})
export class AppRoutingModule { }
