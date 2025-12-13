import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { SectionService } from '../../../../core/services/section.service';
import { PictogramService } from '../../../../core/services/pictogram.service';

@Component({
  selector: 'app-crear-editar-seccion',
  templateUrl: './crear-editar-seccion.component.html',
  styleUrls: ['./crear-editar-seccion.component.css'],
  standalone: false
})
export class CrearEditarSeccionComponent implements OnChanges {
  @Input() ownerId: string = '';
  @Input() seccion: any = null;
  @Output() cancelar = new EventEmitter<void>();
  @Output() seccionCreada = new EventEmitter<any>();

  form = {
    nombre: '',
    language: '',
    imageId: ''
  };

  languages: any[] = [];
  selectedImage: any = null;
  pictogramasDisponibles: any[] = [];
  pictogramasEnSeccion: any[] = new Array(24).fill(null); // 24 pictogramas máximo
  draggedPictogram: any = null;
  draggedFromIndex: number | null = null;
  loading: boolean = false;
  error: string = '';
  success: boolean = false;
  formCollapsed: boolean = false; // Control de formulario colapsado
  layoutMode: 'vertical' | 'horizontal' = 'vertical'; // Modo de visualización

  constructor(
    private sectionService: SectionService,
    private pictogramService: PictogramService
  ) {
    this.loadLanguages();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['seccion']) {
      this.syncFormWithSeccion();
    }
    if (changes['ownerId']) {
      this.loadPictogramas();
    }
  }

  loadLanguages(): void {
    this.languages = [
      { code: 'es', name: 'Español' },
      { code: 'en', name: 'English' },
      { code: 'fr', name: 'Français' },
      { code: 'de', name: 'Deutsch' },
      { code: 'pt', name: 'Portugués' }
    ];
  }

  loadPictogramas(): void {
    if (!this.ownerId) return;

    this.pictogramService.getAllPictograms(this.ownerId).subscribe({
      next: (data) => {
        this.pictogramasDisponibles = data;
      },
      error: (err) => {
        console.error('Error cargando pictogramas:', err);
      }
    });
  }

  onImageSelected(image: any): void {
    this.selectedImage = image;
    this.form.imageId = image.id;
  }

  onDragStart(pictogram: any, event: DragEvent): void {
    this.draggedPictogram = pictogram;
    this.draggedFromIndex = null; // Clear when dragging from available list
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move';
    }
  }

  onDragStartFromPreview(pictogram: any, index: number, event: DragEvent): void {
    this.draggedPictogram = pictogram;
    this.draggedFromIndex = index; // Track source index
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move';
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = 'move';
    }
  }

  onDropInSlot(index: number, event: DragEvent): void {
    event.preventDefault();
    if (!this.draggedPictogram) return;

    // Si estamos arrastrando desde el preview (reordenando)
    if (this.draggedFromIndex !== null) {
      // Intercambiar posiciones
      const temp = this.pictogramasEnSeccion[index];
      this.pictogramasEnSeccion[index] = this.draggedPictogram;
      this.pictogramasEnSeccion[this.draggedFromIndex] = temp;
    } else {
      // Arrastrando desde la lista de disponibles
      // Remover el pictograma de donde estaba si ya está en la sección
      this.pictogramasEnSeccion = this.pictogramasEnSeccion.map(p =>
        p?.id === this.draggedPictogram.id ? null : p
      );

      // Agregarlo en el índice
      this.pictogramasEnSeccion[index] = this.draggedPictogram;
    }

    // Compactar: mover todos los pictogramas hacia arriba eliminando espacios
    this.compactarPictogramas();

    this.draggedPictogram = null;
    this.draggedFromIndex = null;
  }

  compactarPictogramas(): void {
    const pictogramas = this.pictogramasEnSeccion.filter(p => p !== null);
    this.pictogramasEnSeccion = [...pictogramas, ...new Array(24 - pictogramas.length).fill(null)];
  }

  removePictogramFromSlot(index: number): void {
    this.pictogramasEnSeccion[index] = null;
    this.compactarPictogramas();
  }

  toggleFormCollapse(): void {
    this.formCollapsed = !this.formCollapsed;
  }

  toggleLayoutMode(): void {
    this.layoutMode = this.layoutMode === 'vertical' ? 'horizontal' : 'vertical';
  }

  get gridColumns(): number {
    return this.layoutMode === 'vertical' ? 4 : 8;
  }

  get gridRows(): number {
    return this.layoutMode === 'vertical' ? 6 : 3;
  }

  private resetForm(): void {
    this.form = {
      nombre: '',
      language: '',
      imageId: ''
    };
    this.selectedImage = null;
    this.pictogramasEnSeccion = new Array(24).fill(null);
    this.success = false;
    this.error = '';
  }

  private syncFormWithSeccion(): void {
    if (this.seccion) {
      this.form.nombre = this.seccion.name || '';
      this.form.language = this.seccion.language?.code || '';
      this.form.imageId = this.seccion.image?.id || '';
      this.selectedImage = this.seccion.image || null;

      // Cargar pictogramas en la sección
      this.pictogramasEnSeccion = new Array(24).fill(null);
      const pictogramas = this.seccion.pictograms || [];
      pictogramas.forEach((p: any, index: number) => {
        if (index < 24) {
          this.pictogramasEnSeccion[index] = p;
        }
      });
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
      this.error = 'Selecciona una imagen de portada';
      return;
    }

    this.loading = true;
    this.error = '';

    // Crear request base
    const request: any = {
      ownerId: this.ownerId,
      name: this.form.nombre,
      imageId: this.form.imageId,
      language: this.form.language
    };

    // Solo agregar pictograms en edición
    if (this.isEdit) {
      request.pictograms = this.pictogramasEnSeccion.filter(p => p !== null).map(p => p.id);
    }

    const request$ = this.isEdit
      ? this.sectionService.updateSection(this.seccion.id, request)
      : this.sectionService.createSection(request);

    request$.subscribe({
      next: (response) => {
        this.success = true;
        this.loading = false;
        this.seccionCreada.emit(response);

        // Si es creación, emitir para que se abra automáticamente en edición
        if (!this.isEdit) {
          setTimeout(() => {
            this.seccionCreada.emit({ ...response, shouldReopen: true });
            this.cancelar.emit();
          }, 400);
        } else {
          setTimeout(() => {
            this.cancelar.emit();
          }, 400);
        }
      },
      error: (err) => {
        console.error('Error guardando sección:', err);
        this.error = 'Error al guardar la sección';
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.cancelar.emit();
  }

  get isEdit(): boolean {
    return !!this.seccion?.id;
  }

  get pictogramasDisponiblesFiltrados(): any[] {
    const usedIds = this.pictogramasEnSeccion
      .filter(p => p !== null)
      .map(p => p.id);
    return this.pictogramasDisponibles.filter(p => !usedIds.includes(p.id));
  }

  compareLanguages(c1: any, c2: any): boolean {
    return c1 && c2 ? c1 === c2 : c1 === c2;
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
}
