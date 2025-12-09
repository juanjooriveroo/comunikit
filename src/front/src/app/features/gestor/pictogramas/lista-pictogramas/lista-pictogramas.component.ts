import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { PictogramService } from '../../../../core/services/pictogram.service';
import { EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-lista-pictogramas',
  templateUrl: './lista-pictogramas.component.html',
  standalone: false
})
export class ListaPictogramasComponent implements OnInit, OnChanges {
  @Input() ownerId: string = '';
  @Input() key: number = 0;
  @Output() editar = new EventEmitter<any>();

  pictogramas: any[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private pictogramService: PictogramService) {}

  ngOnInit(): void {
    this.loadPictogramas();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['key'] && !changes['key'].firstChange) {
      this.loadPictogramas();
    }
  }

  loadPictogramas(): void {
    if (!this.ownerId) return;

    this.loading = true;
    this.error = '';

    this.pictogramService.getAllPictograms(this.ownerId).subscribe({
      next: (data) => {
        this.pictogramas = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error cargando pictogramas:', err);
        this.error = 'Error al cargar pictogramas';
        this.loading = false;
      }
    });
  }

  deletePictogram(id: string): void {
    if (!confirm('¿Estás seguro de que deseas eliminar este pictograma?')) {
      return;
    }

    this.pictogramService.deletePictogram(id, this.ownerId).subscribe({
      next: () => {
        this.pictogramas = this.pictogramas.filter(p => p.id !== id);
      },
      error: (err) => {
        console.error('Error eliminando pictograma:', err);
        this.error = 'Error al eliminar pictograma';
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

  editarPictograma(pictograma: any): void {
    this.editar.emit(pictograma);
  }
}
