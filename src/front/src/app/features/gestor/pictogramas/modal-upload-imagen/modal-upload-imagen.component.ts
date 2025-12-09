import { Component, Input, Output, EventEmitter } from '@angular/core';
import { PictogramService } from '../../../../core/services/pictogram.service';

@Component({
  selector: 'app-modal-upload-imagen',
  templateUrl: './modal-upload-imagen.component.html',
  standalone: false
})
export class ModalUploadImagenComponent {
  @Input() ownerId: string = '';
  @Output() imageUploaded = new EventEmitter<any>();
  @Output() close = new EventEmitter<void>();

  uploading: boolean = false;
  error: string = '';
  selectedFile: File | null = null;
  preview: string | ArrayBuffer | null = null;

  constructor(private pictogramService: PictogramService) {}

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      if (!['image/jpeg', 'image/png'].includes(file.type)) {
        this.error = 'Solo se permiten imágenes JPG o PNG';
        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        this.error = 'La imagen no puede superar 5MB';
        return;
      }

      this.selectedFile = file;
      this.error = '';

      const reader = new FileReader();
      reader.onload = () => {
        this.preview = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }

  uploadImage(): void {
    if (!this.selectedFile || !this.ownerId) {
      this.error = 'Selecciona un archivo';
      return;
    }

    this.uploading = true;
    this.error = '';

    this.pictogramService.uploadImage(this.selectedFile, this.ownerId).subscribe({
      next: (image) => {
        this.uploading = false;
        this.imageUploaded.emit(image);
      },
      error: (err) => {
        console.error('Error subiendo imagen:', err);
        this.error = 'Error al subir la imagen';
        this.uploading = false;
      }
    });
  }

  closeModal(): void {
    this.close.emit();
  }
}
