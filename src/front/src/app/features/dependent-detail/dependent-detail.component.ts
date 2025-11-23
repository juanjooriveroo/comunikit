import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DependentService } from '../../core/services/dependent.service';
import { DependentAccount, EditProfileRequest, ChangePasswordRequest, DeleteAccountRequest } from '../../shared/models/dependent.model';

@Component({
  selector: 'app-dependent-detail',
  templateUrl: './dependent-detail.component.html',
  styleUrls: ['./dependent-detail.component.css']
})
export class DependentDetailComponent implements OnInit {
  dependentAccount: DependentAccount | null = null;
  loading = true;
  error = '';

  // Modo de edición
  isEditingProfile = false;
  isChangingPassword = false;

  // Modal de eliminación
  showDeleteModal = false;

  // Formularios
  editForm: EditProfileRequest = {
    name: '',
    language: '',
    userId: ''
  };

  passwordForm: ChangePasswordRequest = {
    oldPassword: '',
    newPassword: '',
    userId: ''
  };

  // Mensajes
  editMessage = '';
  passwordMessage = '';
  deleteMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dependentService: DependentService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadDependentAccount(id);
    } else {
      this.router.navigate(['/home']);
    }
  }

  loadDependentAccount(id: string): void {
    this.loading = true;
    this.dependentService.getDependentAccount(id).subscribe({
      next: (account) => {
        this.dependentAccount = account;
        this.loading = false;
        this.initializeEditForm();
      },
      error: (error) => {
        console.error('Error al cargar cuenta dependiente:', error);
        this.error = 'No se pudo cargar la información de la cuenta';
        this.loading = false;
      }
    });
  }

  initializeEditForm(): void {
    if (this.dependentAccount) {
      this.editForm = {
        name: this.dependentAccount.name,
        language: this.dependentAccount.language?.code || 'es',
        userId: this.dependentAccount.id
      };
    }
  }

  toggleEditProfile(): void {
    this.isEditingProfile = !this.isEditingProfile;
    if (this.isEditingProfile) {
      this.isChangingPassword = false; // Cerrar cambiar contraseña si está abierto
      this.initializeEditForm();
    }
    this.editMessage = '';
  }

  toggleChangePassword(): void {
    this.isChangingPassword = !this.isChangingPassword;
    if (this.isChangingPassword) {
      this.isEditingProfile = false; // Cerrar editar perfil si está abierto
    }
    this.passwordForm = {
      oldPassword: '',
      newPassword: '',
      userId: this.dependentAccount?.id || ''
    };
    this.passwordMessage = '';
  }

  saveProfile(): void {
    this.editMessage = '';
    this.dependentService.editDependentProfile(this.editForm).subscribe({
      next: () => {
        this.editMessage = 'Perfil actualizado correctamente';
        this.isEditingProfile = false;
        if (this.dependentAccount) {
          this.loadDependentAccount(this.dependentAccount.id);
        }
      },
      error: (error) => {
        console.error('Error al actualizar perfil:', error);
        this.editMessage = 'Error al actualizar el perfil';
      }
    });
  }

  changePassword(): void {
    this.passwordMessage = '';

    if (!this.passwordForm.oldPassword || !this.passwordForm.newPassword) {
      this.passwordMessage = 'Por favor completa todos los campos';
      return;
    }

    if (this.passwordForm.newPassword.length < 8) {
      this.passwordMessage = 'La nueva contraseña debe tener al menos 8 caracteres';
      return;
    }

    this.dependentService.changeDependentPassword(this.passwordForm).subscribe({
      next: () => {
        this.passwordMessage = 'Contraseña cambiada correctamente';
        this.isChangingPassword = false;
        this.passwordForm = {
          oldPassword: '',
          newPassword: '',
          userId: this.dependentAccount?.id || ''
        };
      },
      error: (error) => {
        console.error('Error al cambiar contraseña:', error);
        const errorMsg = error?.error?.message || 'Error al cambiar la contraseña';
        this.passwordMessage = errorMsg;
      }
    });
  }

  openDeleteModal(): void {
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.deleteMessage = '';
  }

  confirmDelete(password: string): void {
    if (!this.dependentAccount) {
      console.error('No hay cuenta dependiente cargada');
      return;
    }

    if (!this.dependentAccount.id) {
      console.error('La cuenta dependiente no tiene ID');
      this.deleteMessage = 'Error: No se puede eliminar la cuenta (ID no disponible)';
      this.showDeleteModal = false;
      return;
    }

    const deleteRequest: DeleteAccountRequest = {
      password: password,
      userId: this.dependentAccount.id
    };

    console.log('Eliminando cuenta dependiente:', {
      accountName: this.dependentAccount.name,
      userId: this.dependentAccount.id
    });

    this.dependentService.deleteDependentAccount(deleteRequest).subscribe({
      next: () => {
        this.deleteMessage = 'Cuenta eliminada correctamente';
        this.showDeleteModal = false;
        // Redirigir al home después de 1 segundo
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 1000);
      },
      error: (error) => {
        console.error('Error al eliminar cuenta:', error);
        const errorMsg = error?.error?.message || 'Error al eliminar la cuenta. Verifica tu contraseña.';
        this.deleteMessage = errorMsg;
        this.showDeleteModal = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  // Métodos placeholder para botones futuros
  openDashboard(): void {
    alert('Funcionalidad de Tablero en desarrollo');
  }

  openPictograms(): void {
    alert('Funcionalidad de Pictogramas y Secciones en desarrollo');
  }
}
