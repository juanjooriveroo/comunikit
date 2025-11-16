import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { UserRole } from '../../shared/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    const allowedRoles = route.data['roles'] as UserRole[];

    if (!allowedRoles || allowedRoles.length === 0) {
      return true;
    }

    if (this.authService.hasAnyRole(allowedRoles)) {
      return true;
    }

    this.router.navigate(['/unauthorized']);
    return false;
  }
}
