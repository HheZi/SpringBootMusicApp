import { NgModule } from '@angular/core';
import { RouteReuseStrategy, RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { HomeComponent } from './home/home.component';
import { SeeTracksComponent } from './home/see-tracks/see-tracks.component';
import { CreatePlaylistComponent } from './home/create-playlist/create-playlist.component';
import { CustomReuseStrategy } from './custome-routes/custom-reuse-strategy';

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
  children: [{path: 'tracks', component: SeeTracksComponent}, {path: 'playlist/create', component: CreatePlaylistComponent}]
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [{provide: RouteReuseStrategy, useClass: CustomReuseStrategy}]
})
export class AppRoutingModule { }
