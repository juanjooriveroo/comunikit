import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { PictogramService } from '../../../../core/services/pictogram.service';

@Component({
  selector: 'app-crear-editar-pictograma',
  templateUrl: './crear-editar-pictograma.component.html',
  standalone: false
})
export class CrearEditarPictogramaComponent implements OnChanges {
  @Input() ownerId: string = '';
  @Input() pictograma: any = null;
  @Output() cancelar = new EventEmitter<void>();
  @Output() pictogramaCreado = new EventEmitter<any>();

  form = {
    nombre: '',
    language: '',
    imageId: ''
  };

  languages: any[] = [];
  selectedImage: any = null;
  loading: boolean = false;
  error: string = '';
  success: boolean = false;

  constructor(private pictogramService: PictogramService) {
    this.loadLanguages();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['pictograma']) {
      this.syncFormWithPictograma();
    }
  }

  loadLanguages(): void {
    this.languages = [
      { code: 'es', name: 'Español' },
      { code: 'en', name: 'English' },
      { code: 'fr', name: 'Français' },
      { code: 'de', name: 'Deutsch' },
      { code: 'pt', name: 'Portugués'}
    ];
  }

  onImageSelected(image: any): void {
    this.selectedImage = image;
    this.form.imageId = image.id;
  }

  private resetForm(): void {
    this.form = {
      nombre: '',
      language: '',
      imageId: ''
    };
    this.selectedImage = null;
    this.success = false;
    this.error = '';
  }

  private syncFormWithPictograma(): void {
    if (this.pictograma) {
      this.form.nombre = this.pictograma.name || '';
      this.form.language = this.pictograma.language?.code || '';
      this.form.imageId = this.pictograma.image?.id || '';
      this.selectedImage = this.pictograma.image || null;
      this.success = false;
      this.error = '';
    } else {
      this.resetForm();
    }
  }

  submit(): void {
    if (!this.form.nombre.trim()) {
      this.error = 'El nombre es requerido';
      return;
    }

    if (!this.form.language) {
      this.error = 'Selecciona un lenguaje';
      return;
    }

    if (!this.form.imageId) {
      this.error = 'Selecciona una imagen';
      return;
    }

    this.loading = true;
    this.error = '';

    const request = {
      ownerId: this.ownerId,
      name: this.form.nombre,
      imageId: this.form.imageId,
      language: this.form.language
    };

    const request$ = this.isEdit
      ? this.pictogramService.updatePictogram(this.pictograma.id, request)
      : this.pictogramService.createPictogram(request);

    request$.subscribe({
      next: (response) => {
        this.success = true;
        this.loading = false;
        this.pictogramaCreado.emit(response);
        setTimeout(() => {
          this.cancelar.emit();
        }, 400);
      },
      error: (err) => {
        console.error('Error guardando pictograma:', err);
        this.error = 'Error al guardar el pictograma';
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.cancelar.emit();
  }

  get isEdit(): boolean {
    return !!this.pictograma?.id;
  }

  compareLanguages(c1: any, c2: any): boolean {
    return c1 && c2 ? c1 === c2 : c1 === c2;
  }
}
