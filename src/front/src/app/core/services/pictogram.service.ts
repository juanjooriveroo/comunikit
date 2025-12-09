import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PictogramService {
  private baseUrl = `${environment.apiUrl}/board`;
  constructor(private http: HttpClient) {}

  /**
   * Devuelve todos los pictogramas del owner indicado.
   */
  getAllPictograms(ownerId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/getAll-pictograms`, {
      params: { ownerId }
    });
  }

  /**
   * Devuelve todas las imágenes del owner indicado.
   */
  getAllImages(ownerId: string): Observable<any[]> {
    console.log('PictogramService.getAllImages - ownerId:', ownerId);
    return this.http.get<any[]>(`${this.baseUrl}/getAll-images`, {
      params: { ownerId }
    });
  }

  /**
   * Crea un pictograma para el owner indicado.
   */
  createPictogram(request: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/pictogram`, request);
  }

  /**
   * Actualiza un pictograma existente.
   */
  updatePictogram(id: string, request: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/pictogram/${id}`, request);
  }

  /**
   * Elimina un pictograma por id.
   */
  deletePictogram(id: string, ownerId: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/pictogram/${id}`, {
      body: { ownerId }
    });
  }

  /**
   * Sube una imagen asociada a un owner.
   */
  uploadImage(file: File, ownerId: string, language: string = 'es', name?: string): Observable<any> {
    const formData = new FormData();

    const data = {
      ownerId: ownerId,
      language: language,
      name: name ?? file.name
    };

    formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    formData.append('file', file);

    return this.http.post<any>(`${this.baseUrl}/image`, formData);
  }

  /**
   * Elimina una imagen por id.
   */
  deleteImage(id: string, ownerId: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/image/${id}`, {
      body: { ownerId }
    });
  }
}
