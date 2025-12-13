/**
 * Interfaces para secciones y pictogramas
 */

export interface Language {
  code: string;
  name: string;
}

export interface ImageDto {
  id: string;
  name: string;
  mimeType: string;
  image?: string;
  imageBytes?: string;
}

export interface PictogramDto {
  id: string;
  name: string;
  language: Language;
  image: ImageDto;
  isPublic: boolean;
}

/**
 * Pictograma con posición en el grid 5x6
 */
export interface PictogramPositionDto {
  col: number;  // 0-4
  row: number;  // 0-5
  pictogram: PictogramDto;
}

/**
 * Request para añadir/actualizar un pictograma en una posición
 */
export interface PictogramPositionRequestDto {
  pictogramId: string;
  col: number;  // 0-4
  row: number;  // 0-5
}

export interface SectionDto {
  id: string;
  name: string;
  language: Language;
  image: ImageDto;
  isPublic: boolean;
  pictograms: PictogramPositionDto[];  // Pictogramas con posiciones en grid 5x6
}

export interface SectionPushRequestDto {
  imageId: string;
  ownerId: string;
  name: string;
  language: string;
}

export interface SectionUpdateRequestDto {
  ownerId: string;
  imageId?: string;
  name?: string;
  language?: string;
  pictograms?: PictogramPositionRequestDto[];  // Pictogramas con posiciones
}

// Constantes del grid
export const GRID_COLS = 5;
export const GRID_ROWS = 6;
export const TOTAL_CELLS = GRID_COLS * GRID_ROWS; // 30
