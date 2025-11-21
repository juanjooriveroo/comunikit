import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, CreateUserRequest, CreateUserResponse } from '../../shared/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  /**
   * Crear un nuevo usuario dependiente (HU02)
   * Solo accesible por TUTOR
   * Devuelve username, password e idUser
   */
  createUser(userData: CreateUserRequest): Observable<CreateUserResponse> {
    return this.http.post<CreateUserResponse>(
      `${this.apiUrl}/create-user`,
      userData
    );
  }

  /**
   * Obtener todos los usuarios gestionados por el tutor actual
   */
  getManagedUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiUrl}/users/managed`);
  }

  /**
   * Obtener un usuario específico por ID
   */
  getUser(id: string): Observable<User> {
    return this.http.get<User>(`${environment.apiUrl}/users/${id}`);
  }

  /**
   * Actualizar un usuario
   */
  updateUser(id: string, userData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${environment.apiUrl}/users/${id}`, userData);
  }

  /**
   * Eliminar un usuario
   */
  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/users/${id}`);
  }

  /**
   * Resetear contraseña de un usuario
   */
  resetPassword(id: string): Observable<{ newPassword: string }> {
    return this.http.post<{ newPassword: string }>(
      `${environment.apiUrl}/users/${id}/reset-password`,
      {}
    );
  }
}
