import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { BoardService } from '../../../core/services/board.service';
import { SectionService } from '../../../core/services/section.service';
import { PictogramService } from '../../../core/services/pictogram.service';
import {
  BoardDto,
  BoardUpdateRequestDto,
  SectionPositionDto,
  SectionPositionRequestDto,
  PictogramPositionBoardRequestDto,
  BoardItemType,
  BOARD_GRID_COLS,
  BOARD_GRID_ROWS
} from '../../../shared/models/board.model';
import { SectionDto, PictogramDto } from '../../../shared/models/section.model';

interface GridCell {
  col: number;
  row: number;
  item: GridItem | null;
}

interface GridItem {
  id: string;
  name: string;
  type: BoardItemType;
  imageUrl: string;
}

interface DraggableItem {
  id: string;
  name: string;
  type: BoardItemType;
  imageUrl: string;
  isPublic: boolean;
}

@Component({
  selector: 'app-editar-tablero',
  templateUrl: './editar-tablero.component.html',
  styleUrls: ['./editar-tablero.component.css'],
  standalone: false
})
export class EditarTableroComponent implements OnInit, OnChanges {
  @Input() ownerId: string = '';
  @Input() key: number = 0;

  board: BoardDto | null = null;
  mySections: SectionDto[] = [];
  myPictograms: PictogramDto[] = [];
  publicSections: SectionDto[] = [];
  grid: GridCell[][] = [];

  loading = true;
  saving = false;
  error = '';
  successMessage = '';

  // Para drag and drop
  draggedItem: DraggableItem | null = null;
  draggedFromGrid: GridCell | null = null;

  // Columnas y filas del grid
  readonly COLS = BOARD_GRID_COLS;
  readonly ROWS = BOARD_GRID_ROWS;

  constructor(
    private boardService: BoardService,
    private sectionService: SectionService,
    private pictogramService: PictogramService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['key'] && !changes['key'].firstChange) {
      this.loadData();
    }
    if (changes['ownerId'] && !changes['ownerId'].firstChange) {
      this.loadData();
    }
  }

  loadData(): void {
    if (!this.ownerId) return;

    this.loading = true;
    this.error = '';

    // Cargar tablero
    this.boardService.getBoard(this.ownerId).subscribe({
      next: (board) => {
        this.board = board;
        this.initializeGrid();
        this.loadSections();
        this.loadPictograms();
      },
      error: (err) => {
        console.error('Error cargando tablero:', err);
        this.error = 'Error al cargar el tablero';
        this.loading = false;
      }
    });
  }

  loadSections(): void {
    // Cargar secciones del dependiente (propias)
    this.sectionService.getAllSections(this.ownerId).subscribe({
      next: (sections) => {
        this.mySections = sections;
        this.loadPublicSections();
      },
      error: (err) => {
        console.error('Error cargando secciones:', err);
        this.loading = false;
      }
    });
  }

  loadPublicSections(): void {
    // Cargar secciones públicas
    const languageCode = this.board?.languageCode || 'es';
    this.boardService.getPublicSections(languageCode).subscribe({
      next: (sections) => {
        this.publicSections = sections;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error cargando secciones públicas:', err);
        this.publicSections = [];
        this.loading = false;
      }
    });
  }

  loadPictograms(): void {
    // Cargar pictogramas del dependiente
    this.pictogramService.getAllPictograms(this.ownerId).subscribe({
      next: (pictograms) => {
        this.myPictograms = pictograms;
      },
      error: (err) => {
        console.error('Error cargando pictogramas:', err);
        this.myPictograms = [];
      }
    });
  }

  initializeGrid(): void {
    // Crear grid vacío
    this.grid = [];
    for (let row = 0; row < this.ROWS; row++) {
      const rowCells: GridCell[] = [];
      for (let col = 0; col < this.COLS; col++) {
        rowCells.push({
          col,
          row,
          item: null
        });
      }
      this.grid.push(rowCells);
    }

    // Colocar las secciones del tablero en el grid
    if (this.board?.sections) {
      for (const section of this.board.sections) {
        if (section.col >= 0 && section.col < this.COLS &&
            section.row >= 0 && section.row < this.ROWS) {
          this.grid[section.row][section.col].item = {
            id: section.sectionId,
            name: section.name,
            type: 'section',
            imageUrl: section.imageUrl || ''
          };
        }
      }
    }

    // Colocar los pictogramas del tablero en el grid
    if (this.board?.pictograms) {
      for (const pictogram of this.board.pictograms) {
        if (pictogram.col >= 0 && pictogram.col < this.COLS &&
            pictogram.row >= 0 && pictogram.row < this.ROWS) {
          this.grid[pictogram.row][pictogram.col].item = {
            id: pictogram.pictogramId,
            name: pictogram.name,
            type: 'pictogram',
            imageUrl: pictogram.imageUrl || ''
          };
        }
      }
    }
  }

  // Combina secciones propias y públicas para mostrar
  get allAvailableSections(): SectionDto[] {
    return [...this.mySections, ...this.publicSections];
  }

  // === DRAG AND DROP ===

  onDragStartFromSection(event: DragEvent, section: SectionDto, isPublic: boolean): void {
    this.draggedItem = {
      id: section.id,
      name: section.name,
      type: 'section',
      imageUrl: this.getSectionImageUrl(section),
      isPublic
    };
    this.draggedFromGrid = null;
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move';
    }
  }

  onDragStartFromPictogram(event: DragEvent, pictogram: PictogramDto): void {
    this.draggedItem = {
      id: pictogram.id,
      name: pictogram.name,
      type: 'pictogram',
      imageUrl: this.getPictogramImageUrl(pictogram),
      isPublic: false
    };
    this.draggedFromGrid = null;
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move';
    }
  }

  onDragStartFromGrid(event: DragEvent, cell: GridCell): void {
    if (cell.item) {
      this.draggedItem = null;
      this.draggedFromGrid = cell;
      if (event.dataTransfer) {
        event.dataTransfer.effectAllowed = 'move';
      }
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = 'move';
    }
  }

  onDropOnCell(event: DragEvent, targetCell: GridCell): void {
    event.preventDefault();

    if (this.draggedItem) {
      // Arrastrar desde la lista
      this.placeItemOnCell(this.draggedItem, targetCell);
    } else if (this.draggedFromGrid) {
      // Mover dentro del grid
      this.moveItemInGrid(this.draggedFromGrid, targetCell);
    }

    this.draggedItem = null;
    this.draggedFromGrid = null;
  }

  onDropOnTrash(event: DragEvent): void {
    event.preventDefault();

    if (this.draggedFromGrid && this.draggedFromGrid.item) {
      // Eliminar item del grid
      this.draggedFromGrid.item = null;
    }

    this.draggedItem = null;
    this.draggedFromGrid = null;
  }

  onDragEnd(): void {
    this.draggedItem = null;
    this.draggedFromGrid = null;
  }

  private placeItemOnCell(item: DraggableItem, targetCell: GridCell): void {
    // Si la celda ya tiene un item, no hacer nada
    if (targetCell.item) {
      return;
    }

    // Verificar si el item ya está en el grid en otra posición
    for (const row of this.grid) {
      for (const cell of row) {
        if (cell.item?.id === item.id && cell.item?.type === item.type) {
          cell.item = null;
          break;
        }
      }
    }

    // Colocar el item en la celda objetivo
    targetCell.item = {
      id: item.id,
      name: item.name,
      type: item.type,
      imageUrl: item.imageUrl
    };
  }

  private moveItemInGrid(fromCell: GridCell, toCell: GridCell): void {
    if (!fromCell.item) return;

    if (toCell.item) {
      // Intercambiar items
      const tempItem = toCell.item;
      toCell.item = { ...fromCell.item };
      fromCell.item = { ...tempItem };
    } else {
      // Mover a celda vacía
      toCell.item = { ...fromCell.item };
      fromCell.item = null;
    }
  }

  // === ACCIONES ===

  saveBoard(): void {
    if (!this.board) return;

    this.saving = true;
    this.error = '';
    this.successMessage = '';

    // Recolectar todas las secciones y pictogramas del grid
    const sections: SectionPositionRequestDto[] = [];
    const pictograms: PictogramPositionBoardRequestDto[] = [];

    for (const row of this.grid) {
      for (const cell of row) {
        if (cell.item) {
          if (cell.item.type === 'section') {
            sections.push({
              sectionId: cell.item.id,
              col: cell.col,
              row: cell.row
            });
          } else if (cell.item.type === 'pictogram') {
            pictograms.push({
              pictogramId: cell.item.id,
              col: cell.col,
              row: cell.row
            });
          }
        }
      }
    }

    const request: BoardUpdateRequestDto = { sections, pictograms };

    this.boardService.updateBoard(this.ownerId, request).subscribe({
      next: (updatedBoard) => {
        this.board = updatedBoard;
        this.successMessage = 'Tablero guardado correctamente';
        this.saving = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('Error guardando tablero:', err);
        this.error = err?.error?.message || 'Error al guardar el tablero';
        this.saving = false;
      }
    });
  }

  resetBoard(): void {
    if (confirm('¿Estás seguro de descartar los cambios?')) {
      this.initializeGrid();
    }
  }

  clearBoard(): void {
    if (confirm('¿Estás seguro de limpiar todo el tablero?')) {
      for (const row of this.grid) {
        for (const cell of row) {
          cell.item = null;
        }
      }
    }
  }

  // Verifica si una sección ya está en el tablero
  isSectionInBoard(sectionId: string): boolean {
    for (const row of this.grid) {
      for (const cell of row) {
        if (cell.item?.id === sectionId && cell.item?.type === 'section') {
          return true;
        }
      }
    }
    return false;
  }

  // Verifica si un pictograma ya está en el tablero
  isPictogramInBoard(pictogramId: string): boolean {
    for (const row of this.grid) {
      for (const cell of row) {
        if (cell.item?.id === pictogramId && cell.item?.type === 'pictogram') {
          return true;
        }
      }
    }
    return false;
  }

  // Obtener imagen de sección
  getSectionImageUrl(section: any): string {
    if (section?.image) {
      const payload = section.image.image || section.image.imageBytes;
      if (payload) {
        return `data:${section.image.mimeType};base64,${payload}`;
      }
    }
    return 'assets/placeholder.png';
  }

  // Obtener imagen de pictograma
  getPictogramImageUrl(pictogram: any): string {
    if (pictogram?.image) {
      const payload = pictogram.image.image || pictogram.image.imageBytes;
      if (payload) {
        return `data:${pictogram.image.mimeType};base64,${payload}`;
      }
    }
    return 'assets/placeholder.png';
  }

  // Verifica si es sección pública
  isPublicSection(sectionId: string): boolean {
    return this.publicSections.some(s => s.id === sectionId);
  }
}
