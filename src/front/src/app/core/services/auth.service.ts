import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
  User,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
  TokenPayload,
  UserRole
} from '../../shared/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUserFromStorage();
  }

  /**
   * Cargar usuario del localStorage al iniciar
   */
  private loadUserFromStorage(): void {
    const token = this.getToken();
    if (token && this.isTokenValid()) {
      const user = this.decodeTokenToUser(token);
      if (user) {
        this.currentUserSubject.next(user);
      }
    } else {
      localStorage.removeItem('token');
      this.currentUserSubject.next(null);
    }
  }

  /**
   * Decodificar JWT token y extraer usuario
   */
  private decodeTokenToUser(token: string): User | null {
    try {
      const payload = this.decodeToken(token);
      if (!payload) return null;

      return {
        id: payload.sub,
        email: payload.email,
        name: payload.name,
        role: payload.role,
        language: payload.language
      };
    } catch (e) {
      console.error('Error al decodificar token:', e);
      return null;
    }
  }

  /**
   * Decodificar payload del JWT
   */
  private decodeToken(token: string): TokenPayload | null {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload;
    } catch (e) {
      return null;
    }
  }

  /**
   * Login del usuario
   */
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${environment.apiUrl}/auth/login`,
      credentials
    ).pipe(
      tap(response => this.handleAuthResponse(response)),
      catchError(error => {
        console.error('Error en login:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Registro de nuevo tutor
   */
  register(userData: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(
      `${environment.apiUrl}/auth/register`,
      userData
    ).pipe(
      tap(response => {
        if (response.request) {
          console.log('Registro exitoso. Revisa tu email para activar la cuenta.');
        }
      }),
      catchError(error => {
        console.error('Error en registro:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Activar cuenta mediante token del email
   */
  activateAccount(activationToken: string): Observable<any> {
    return this.http.post<any>(
      `${environment.apiUrl}/auth/activate/${activationToken}`,
      {}
    ).pipe(
      catchError(error => {
        console.error('Error al activar cuenta:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Manejar respuesta de autenticación
   */
  private handleAuthResponse(response: LoginResponse): void {
    localStorage.setItem('token', response.token);

    const user = this.decodeTokenToUser(response.token);
    if (user) {
      this.currentUserSubject.next(user);
    }
  }

  /**
   * Logout del usuario
   */
  logout(): void {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  /**
   * Obtener el token JWT
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * Decodificar token actual
   */
  getDecodedToken(): TokenPayload | null {
    const token = this.getToken();
    return token ? this.decodeToken(token) : null;
  }

  /**
   * Verificar si el token es válido
   */
  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = this.decodeToken(token);
      if (!payload) return false;

      const exp = payload.exp * 1000;
      return Date.now() < exp;
    } catch (e) {
      return false;
    }
  }

  /**
   * Verificar si el usuario está autenticado
   */
  isAuthenticated(): boolean {
    return this.isTokenValid();
  }

  /**
   * Obtener el usuario actual
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Verificar si el usuario tiene un rol específico
   */
  hasRole(role: UserRole): boolean {
    const user = this.getCurrentUser();
    return user?.role === role;
  }

  /**
   * Verificar si el usuario tiene alguno de los roles especificados
   */
  hasAnyRole(roles: UserRole[]): boolean {
    const user = this.getCurrentUser();
    return user ? roles.includes(user.role) : false;
  }

  /**
   * Verificar si es tutor o admin
   */
  canManageUsers(): boolean {
    return this.hasAnyRole([UserRole.TUTOR, UserRole.ADMIN]);
  }

  /**
   * Solicitar recuperación de cuenta
   */
  recoveryAccount(email: string): Observable<any> {
    const url = `${environment.apiUrl}/auth/recovery-account`;
    return this.http.post(url, { email }).pipe(
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }

  /**
   * Confirmar nueva contraseña
   */
  confirmNewPassword(resetData: { userId: string; newPassword: string }): Observable<any> {
    const url = `${environment.apiUrl}/auth/confirm-new-password`;
    return this.http.post(url, resetData).pipe(
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }

  /**
   * Solicitar borrado de cuenta (envía correo de confirmación)
   */
  deleteRequest(): Observable<any> {
    const url = `${environment.apiUrl}/auth/delete-request`;
    return this.http.post(url, {}).pipe(
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }

  /**
   * Confirmar eliminación de cuenta con contraseña
   */
  deleteAccount(deleteData: { password: string }): Observable<any> {
    const url = `${environment.apiUrl}/auth/delete-account`;
    return this.http.delete(url, {
      body: deleteData
    }).pipe(
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }

  /**
   * Editar perfil del usuario actual o de un usuario dependiente
   */
  editProfile(profileData: { name?: string; email?: string; language?: string; userId?: string }): Observable<any> {
    const url = `${environment.apiUrl}/auth/edit-profile`;
    return this.http.put(url, profileData).pipe(
      tap(() => {
        // Actualizar el usuario en memoria si se cambió algún dato
        const currentUser = this.getCurrentUser();
        if (currentUser) {
          if (profileData.name) currentUser.name = profileData.name;
          if (profileData.email) currentUser.email = profileData.email;
          if (profileData.language) currentUser.language = profileData.language;
          this.currentUserSubject.next(currentUser);
        }
      }),
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }

  /**
   * Cambiar contraseña del usuario actual
   */
  changePassword(passwordData: { oldPassword: string; newPassword: string }): Observable<any> {
    const url = `${environment.apiUrl}/auth/change-password`;
    return this.http.put(url, passwordData).pipe(
      catchError((error) => {
        return throwError(() => error);
      })
    );
  }
}
