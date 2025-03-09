import { inject } from '@angular/core';
import { CanActivateChildFn, Router} from '@angular/router';
import { AuthService } from './services/auth/auth.service';
import { map } from 'rxjs';
import { MessageService } from 'primeng/api';

export const adminGuard: CanActivateChildFn = () => {
  const authService = inject(AuthService);

  if (!authService.isUserAuthenticated()){
    return false;
  }

  return authService.isAdmin$.pipe(
    map((val: boolean) => {
      return val;
    })
  );
};
