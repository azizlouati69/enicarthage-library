import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';

export const AuthGuard: CanActivateFn = (route, state) => {
  // Temporarily allow all access for screenshots
  return true;
  
  // Original code (commented out for screenshots):
  // const authService = inject(AuthService);
  // const router = inject(Router);

  // return authService.isAuthenticated$.pipe(
  //   take(1),
  //   map(isAuthenticated => {
  //     if (isAuthenticated) {
  //       return true;
  //     } else {
  //       router.navigate(['/login']);
  //       return false;
  //     }
  //   })
  // );
};