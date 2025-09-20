import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';

export const RoleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const requiredRoles = route.data?.['roles'] as string[];

  if (!requiredRoles || requiredRoles.length === 0) {
    return true;
  }

  return authService.currentUser$.pipe(
    take(1),
    map(user => {
      if (user && authService.hasAnyRole(requiredRoles)) {
        return true;
      } else {
        router.navigate(['/dashboard']);
        return false;
      }
    })
  );
};