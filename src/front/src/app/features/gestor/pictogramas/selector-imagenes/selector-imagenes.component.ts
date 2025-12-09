import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { PictogramService } from '../../../../core/services/pictogram.service';

@Component({
  selector: 'app-selector-imagenes',
  templateUrl: './selector-imagenes.component.html',
  standalone: false
})
export class SelectorImagenesComponent implements OnInit {
  @Input() ownerId: string = '';
  @Input() selectedImageId: string | null = null;
  @Output() imageSelected = new EventEmitter<any>();

  imagenes: any[] = [];
  loading: boolean = false;
  error: string = '';
  showUploadModal: boolean = false;

  constructor(private pictogramService: PictogramService) {}

  ngOnInit(): void {
    this.loadImages();
  }

  loadImages(): void {
    if (!this.ownerId) return;

    this.loading = true;
    this.error = '';

    this.pictogramService.getAllImages(this.ownerId).subscribe({
      next: (data) => {
        this.imagenes = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error cargando imágenes:', err);
        this.error = 'Error al cargar imágenes';
        this.loading = false;
      }
    });
  }

  openUploadModal(): void {
    this.showUploadModal = true;
  }

  closeUploadModal(): void {
    this.showUploadModal = false;
  }

  onImageUploaded(image: any): void {
    this.imagenes.push(image);
    this.closeUploadModal();
    this.selectImage(image);
  }

  selectImage(image: any): void {
    this.selectedImageId = image.id;
    this.imageSelected.emit(image);
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

  deleteImage(image: any, event: MouseEvent): void {
    event.stopPropagation();
    if (!image?.id) {
      return;
    }
    this.pictogramService.deleteImage(image.id, this.ownerId).subscribe({
      next: () => {
        this.imagenes = this.imagenes.filter((img) => img.id !== image.id);
        if (this.selectedImageId === image.id) {
          this.selectedImageId = null;
          this.imageSelected.emit(null);
        }
      },
      error: (err) => {
        console.error('Error eliminando imagen:', err);
        this.error = 'No se pudo eliminar la imagen';
      }
    });
  }

  isSelected(imageId: string): boolean {
    return this.selectedImageId === imageId;
  }
}
