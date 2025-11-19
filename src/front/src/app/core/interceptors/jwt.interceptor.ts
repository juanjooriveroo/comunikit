import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();
    const decodedToken = this.authService.getDecodedToken();

    if (token) {
      const headers: any = {
        Authorization: `Bearer ${token}`
      };

      // Agregar el header X-User-ID si el token contiene el ID del usuario
      if (decodedToken && decodedToken.sub) {
        headers['X-User-ID'] = decodedToken.sub;
      }

      request = request.clone({
        setHeaders: headers
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          if (!request.url.includes('/delete-account') && !request.url.includes('/deleteAccount')) {
            this.authService.logout();
            this.router.navigate(['/login'], {
              queryParams: { expired: 'true' }
            });
          }
        }
        return throwError(() => error);
      })
    );
  }
}
