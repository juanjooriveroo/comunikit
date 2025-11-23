import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  DependentAccount,
  GetAllDependentsResponse,
  EditProfileRequest,
  ChangePasswordRequest,
  DeleteAccountRequest
} from '../../shared/models/dependent.model';

@Injectable({
  providedIn: 'root'
})
export class DependentService {
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  /**
   * Obtener todas las cuentas dependientes del tutor actual
   */
  getAllDependents(): Observable<GetAllDependentsResponse> {
    return this.http.get<GetAllDependentsResponse>(`${this.apiUrl}/get-dependents-accounts`);
  }

  /**
   * Obtener información detallada de una cuenta dependiente específica
   */
  getDependentAccount(id: string): Observable<DependentAccount> {
    return this.http.get<DependentAccount>(`${this.apiUrl}/get/${id}`);
  }

  /**
   * Editar perfil de una cuenta dependiente
   */
  editDependentProfile(data: EditProfileRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/edit-profile`, data);
  }

  /**
   * Cambiar contraseña de una cuenta dependiente
   */
  changeDependentPassword(data: ChangePasswordRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/change-password`, data);
  }

  /**
   * Eliminar una cuenta dependiente
   */
  deleteDependentAccount(data: DeleteAccountRequest): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete-account`, { body: data });
  }
}
