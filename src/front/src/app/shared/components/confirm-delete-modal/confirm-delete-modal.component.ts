import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-delete-modal',
  templateUrl: './confirm-delete-modal.component.html',
  styleUrls: ['./confirm-delete-modal.component.css']
})
export class ConfirmDeleteModalComponent {
  @Input() isOpen = false;
  @Input() accountName = '';
  @Output() confirm = new EventEmitter<string>();
  @Output() cancel = new EventEmitter<void>();

  password = '';
  errorMessage = '';

  onConfirm(): void {
    if (!this.password || this.password.length < 8) {
      this.errorMessage = 'La contraseña debe tener al menos 8 caracteres';
      return;
    }
    this.confirm.emit(this.password);
    this.resetModal();
  }

  onCancel(): void {
    this.cancel.emit();
    this.resetModal();
  }

  private resetModal(): void {
    this.password = '';
    this.errorMessage = '';
  }

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.onCancel();
    }
  }
}
