/**
 * Interfaces para el tablero de comunicación
 */

import { SectionDto, PictogramDto } from './section.model';

/**
 * Tipo de elemento en el tablero
 */
export type BoardItemType = 'section' | 'pictogram';

/**
 * Sección posicionada en el tablero (grid 5x6)
 */
export interface SectionPositionDto {
  sectionId: string;
  name: string;
  col: number;  // 0-4
  row: number;  // 0-5
  imageUrl?: string;  // URL de la imagen para mostrar
}

/**
 * Pictograma posicionado en el tablero (grid 5x6)
 */
export interface PictogramPositionBoardDto {
  pictogramId: string;
  name: string;
  col: number;  // 0-4
  row: number;  // 0-5
  imageUrl?: string;  // URL de la imagen para mostrar
}

/**
 * Request para posicionar una sección en el tablero
 */
export interface SectionPositionRequestDto {
  sectionId: string;
  col: number;  // 0-4
  row: number;  // 0-5
}

/**
 * Request para posicionar un pictograma en el tablero
 */
export interface PictogramPositionBoardRequestDto {
  pictogramId: string;
  col: number;  // 0-4
  row: number;  // 0-5
}

/**
 * Tablero de comunicación completo
 */
export interface BoardDto {
  id: string;
  ownerId: string;
  languageCode: string;
  isPublic: boolean;
  sections: SectionPositionDto[];
  pictograms?: PictogramPositionBoardDto[];  // Para futuro soporte
}

/**
 * Request para actualizar las secciones de un tablero
 */
export interface BoardUpdateRequestDto {
  sections: SectionPositionRequestDto[];
  pictograms?: PictogramPositionBoardRequestDto[];  // Para futuro soporte
}

// Constantes del grid del tablero
export const BOARD_GRID_COLS = 5;
export const BOARD_GRID_ROWS = 6;
export const BOARD_TOTAL_CELLS = BOARD_GRID_COLS * BOARD_GRID_ROWS; // 30
