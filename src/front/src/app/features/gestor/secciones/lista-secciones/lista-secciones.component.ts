import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { SectionService } from '../../../../core/services/section.service';
import { EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-lista-secciones',
  templateUrl: './lista-secciones.component.html',
  standalone: false
})
export class ListaSectionesComponent implements OnInit, OnChanges {
  @Input() ownerId: string = '';
  @Input() key: number = 0;
  @Output() editar = new EventEmitter<any>();

  secciones: any[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private sectionService: SectionService) {}

  ngOnInit(): void {
    this.loadSecciones();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['key'] && !changes['key'].firstChange) {
      this.loadSecciones();
    }
  }

  loadSecciones(): void {
    if (!this.ownerId) return;

    this.loading = true;
    this.error = '';

    this.sectionService.getAllSections(this.ownerId).subscribe({
      next: (data) => {
        this.secciones = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error cargando secciones:', err);
        this.error = 'Error al cargar secciones';
        this.loading = false;
      }
    });
  }

  deleteSection(id: string): void {
    if (!confirm('¿Estás seguro de que deseas eliminar esta sección?')) {
      return;
    }

    this.sectionService.deleteSection(id, this.ownerId).subscribe({
      next: () => {
        this.secciones = this.secciones.filter(s => s.id !== id);
      },
      error: (err) => {
        console.error('Error eliminando sección:', err);
        this.error = 'Error al eliminar sección';
      }
    });
  }

  getImageUrl(imageData: any): string {
    if (imageData) {
      const payload = imageData.image || imageData.imageBytes;
      if (payload) {
        return `data:${imageData.mimeType};base64,${payload}`;
      }
    }
    return 'assets/placeholder.png';
  }

  editarSeccion(seccion: any): void {
    this.editar.emit(seccion);
  }
}
