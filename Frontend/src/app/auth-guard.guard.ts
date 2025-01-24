import { inject } from '@angular/core';
import { CanActivateChildFn, Router} from '@angular/router';
import { AuthService } from './services/auth/auth.service';

export const authGuard: CanActivateChildFn = (route, state) => {
  const authService: AuthService = inject(AuthService);
  const router: Router = inject(Router);

  const isAuth: boolean = authService.isUserAuthenticated();

  if(isAuth){
    router.navigate(['login']);
    return false;
  }
  return true;
};
