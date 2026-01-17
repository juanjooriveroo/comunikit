import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BoardDto, BoardUpdateRequestDto } from '../../shared/models/board.model';
import { SectionDto } from '../../shared/models/section.model';

@Injectable({
  providedIn: 'root'
})
export class BoardService {
  private baseUrl = `${environment.apiUrl}/board`;

  constructor(private http: HttpClient) {}

  /**
   * Obtiene el tablero de un usuario dependiente.
   */
  getBoard(dependentId: string): Observable<BoardDto> {
    return this.http.get<BoardDto>(`${this.baseUrl}/board/${dependentId}`);
  }

  /**
   * Obtiene el tablero público de un idioma.
   */
  getPublicBoard(languageCode: string): Observable<BoardDto> {
    return this.http.get<BoardDto>(`${this.baseUrl}/board-public/${languageCode}`);
  }

  /**
   * Actualiza las secciones de un tablero.
   */
  updateBoard(dependentId: string, request: BoardUpdateRequestDto): Observable<BoardDto> {
    return this.http.put<BoardDto>(`${this.baseUrl}/board/${dependentId}`, request);
  }

  /**
   * Obtiene las secciones públicas (opcionalmente filtradas por idioma).
   */
  getPublicSections(languageCode?: string): Observable<SectionDto[]> {
    if (languageCode) {
      return this.http.get<SectionDto[]>(`${this.baseUrl}/sections-publics`, {
        params: { language: languageCode }
      });
    }
    return this.http.get<SectionDto[]>(`${this.baseUrl}/sections-publics`);
  }
}
