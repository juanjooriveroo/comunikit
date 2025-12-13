import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SectionService {
  private baseUrl = `${environment.apiUrl}/board`;

  constructor(private http: HttpClient) {}

  /**
   * Devuelve todas las secciones del owner indicado.
   */
  getAllSections(ownerId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/getAll-section`, {
      params: { ownerId }
    });
  }

  /**
   * Crea una sección para el owner indicado.
   */
  createSection(request: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/section`, request);
  }

  /**
   * Actualiza una sección existente.
   */
  updateSection(id: string, request: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/section/${id}`, request);
  }

  /**
   * Elimina una sección por id.
   */
  deleteSection(id: string, ownerId: string): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/section/${id}`, {
      body: { ownerId }
    });
  }
}
