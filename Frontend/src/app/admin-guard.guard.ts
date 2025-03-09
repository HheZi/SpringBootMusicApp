import { inject } from '@angular/core';
import { CanActivateChildFn, Router} from '@angular/router';
import { AuthService } from './services/auth/auth.service';
import { map } from 'rxjs';
import { MessageService } from 'primeng/api';

export const adminGuard: CanActivateChildFn = () => {
  const authService = inject(AuthService);
  const messageService = inject(MessageService)

  if (!authService.isUserAuthenticated()){
    return false;
  }

  return authService.isAdmin$.pipe(
    map((response: any) => {
      if (response.isAdmin) {
        return true;
      } else {
        messageService.add({closable: true, detail: "You are not the admin", severity: "error"})
        return false;
      }
    })
  );
};
