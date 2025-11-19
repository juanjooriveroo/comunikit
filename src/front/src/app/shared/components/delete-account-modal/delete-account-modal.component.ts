import { Component, OnInit, OnDestroy, ViewChild, ElementRef, Input, Output, EventEmitter } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

declare var bootstrap: any;

@Component({
  selector: 'app-delete-account-modal',
  templateUrl: './delete-account-modal.component.html',
  styleUrls: ['./delete-account-modal.component.css']
})
export class DeleteAccountModalComponent implements OnInit, OnDestroy {
  @Input() modalId = 'deleteAccountModal';
  @Output() onDeleteRequested = new EventEmitter<void>();
  @Output() onCancelled = new EventEmitter<void>();

  deleteRequestLoading = false;
  deleteRequestMessage = '';
  private destroy$ = new Subject<void>();
  @ViewChild('deleteAccountModal') deleteAccountModal!: ElementRef;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Abrir modal de solicitud de borrado
   */
  openModal(): void {
    this.deleteRequestMessage = '';
    this.deleteRequestLoading = false;

    setTimeout(() => {
      const modalElement = document.getElementById(this.modalId);
      if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
      }
    }, 100);
  }

  /**
   * Cerrar modal
   */
  closeModal(): void {
    const modalElement = document.getElementById(this.modalId);
    if (modalElement) {
      const modal = bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    }
  }

  /**
   * Confirmar solicitud de borrado de cuenta
   */
  confirmDeleteRequest(): void {
    this.deleteRequestLoading = true;
    this.deleteRequestMessage = '';

    this.authService.deleteRequest()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.deleteRequestMessage = 'Se ha enviado un correo de confirmación. Revisa tu email para completar el borrado.';
          this.deleteRequestLoading = false;
          this.onDeleteRequested.emit();

          setTimeout(() => {
            this.closeModal();
          }, 2000);
        },
        error: (error: any) => {
          this.deleteRequestLoading = false;

          if (error.status === 404) {
            this.deleteRequestMessage = 'Error: Usuario no encontrado.';
          } else if (error.status === 0) {
            this.deleteRequestMessage = 'Error: No se pudo conectar con el servidor.';
          } else {
            this.deleteRequestMessage = error.error?.message || 'Error: No se pudo procesar la solicitud.';
          }
        }
      });
  }

  /**
   * Cancelar solicitud de borrado
   */
  cancelDeleteRequest(): void {
    this.closeModal();
    this.onCancelled.emit();
  }
}
