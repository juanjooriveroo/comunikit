import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, CreateUserRequest, CreateUserResponse } from '../../shared/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  /**
   * Crear un nuevo usuario final (HU02)
   * Solo accesible por TUTOR o ADMIN       HA DESARROLLAR
   */
  createUser(userData: CreateUserRequest): Observable<CreateUserResponse> {
    return this.http.post<CreateUserResponse>(
      `${this.apiUrl}/create-final-user`,
      userData
    );
  }

  /**
   * Obtener todos los usuarios gestionados por el tutor actual
   */
  getManagedUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/managed`);
  }

  /**
   * Obtener un usuario específico por ID
   */
  getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  /**
   * Actualizar un usuario
   */
  updateUser(id: number, userData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, userData);
  }

  /**
   * Eliminar un usuario
   */
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Resetear contraseña de un usuario
   */
  resetPassword(id: number): Observable<{ newPassword: string }> {
    return this.http.post<{ newPassword: string }>(
      `${this.apiUrl}/${id}/reset-password`,
      {}
    );
  }
}
