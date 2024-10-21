import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { HomeComponent } from './home/home.component';
import { SeeTracksComponent } from './home/see-tracks/see-tracks.component';

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
  children: [{path: 'tracks', component: SeeTracksComponent}]
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
