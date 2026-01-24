// Estado global del tablero
import type { Board, SectionPosition, SelectedPictogram } from './types';

class BoardState {
  private _selectedPictograms: SelectedPictogram[] = [];
  private _boardData: Board | null = null;
  private _sectionsData: Map<string, SectionPosition> = new Map();
  private _listeners: Set<() => void> = new Set();

  get selectedPictograms(): SelectedPictogram[] {
    return [...this._selectedPictograms];
  }

  get boardData(): Board | null {
    return this._boardData;
  }

  get sectionsData(): Map<string, SectionPosition> {
    return this._sectionsData;
  }

  setBoardData(board: Board | null): void {
    this._boardData = board;
    if (board?.sections) {
      board.sections.forEach(section => {
        this._sectionsData.set(section.sectionId, section);
      });
    }
    this.notifyListeners();
  }

  addPictogram(pictogram: SelectedPictogram): void {
    this._selectedPictograms.push(pictogram);
    this.notifyListeners();
  }

  removePictogram(index: number): void {
    this._selectedPictograms.splice(index, 1);
    this.notifyListeners();
  }

  clearPictograms(): void {
    this._selectedPictograms = [];
    this.notifyListeners();
  }

  subscribe(listener: () => void): () => void {
    this._listeners.add(listener);
    return () => this._listeners.delete(listener);
  }

  private notifyListeners(): void {
    this._listeners.forEach(listener => listener());
  }
}

// Singleton del estado
export const boardState = new BoardState();
