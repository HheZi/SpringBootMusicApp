import { CanActivateChildFn, Router } from "@angular/router";
import { AuthService } from "./services/auth/auth.service";
import { inject } from "@angular/core";

export const authGuard: CanActivateChildFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isUserAuthenticated()){
    return true;
  }
  router.navigate(["login"]);
  return false;
};