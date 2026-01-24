// Tipos e interfaces para el tablero

export interface ImageDto {
  id: string;
  imageUrl: string;
}

export interface PictogramDto {
  id: string;
  name: string;
  image: ImageDto;
}

export interface PictogramPosition {
  col: number;
  row: number;
  pictogram: PictogramDto;
}

export interface SectionPosition {
  sectionId: string;
  name: string;
  col: number;
  row: number;
  imageUrl: string;
  pictograms?: PictogramPosition[];
}

export interface PictogramPositionBoard {
  pictogramId: string;
  name: string;
  col: number;
  row: number;
  imageUrl: string;
}

export interface Board {
  id: string;
  sections: SectionPosition[];
  pictograms: PictogramPositionBoard[];
}

export interface SelectedPictogram {
  id: string;
  name: string;
  imageUrl: string;
}

// Constantes - Grid de 12 columnas x 7 filas
export const GRID_COLS = 12;
export const GRID_ROWS = 7;

export const FLAGS: Record<string, string> = {
  es: '🇪🇸',
  en: '🇬🇧',
  fr: '🇫🇷',
  de: '🇩🇪',
  pt: '🇵🇹',
};

export const LANG_CODES: Record<string, string> = {
  es: 'es-ES',
  en: 'en-US',
  fr: 'fr-FR',
  de: 'de-DE',
  pt: 'pt-PT',
};
