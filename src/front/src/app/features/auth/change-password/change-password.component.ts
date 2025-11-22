import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {
  changePasswordForm!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  successMessage = '';
  changeSuccess = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Redirigir si no está autenticado
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.changePasswordForm = this.formBuilder.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  get f() {
    return this.changePasswordForm.controls;
  }

  /**
   * Manejar envío del formulario
   */
  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.changePasswordForm.invalid) {
      return;
    }

    this.loading = true;

    const changePasswordData = {
      oldPassword: this.changePasswordForm.value.oldPassword,
      newPassword: this.changePasswordForm.value.newPassword
    };

    this.authService.changePassword(changePasswordData).subscribe({
      next: () => {
        this.successMessage = '✓ Contraseña actualizada correctamente';
        this.changeSuccess = true;
        this.loading = false;

        // Redirigir al home después de 2 segundos
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 2000);
      },
      error: (error) => {
        this.loading = false;

        if (error.status === 400) {
          this.errorMessage = error.error?.message || 'Los datos de la contraseña no son válidos.';
        } else if (error.status === 401) {
          this.errorMessage = 'La contraseña anterior es incorrecta.';
        } else if (error.status === 404) {
          this.errorMessage = 'Usuario no encontrado.';
        } else if (error.status === 409) {
          this.errorMessage = error.error?.message || 'La contraseña no puede ser la misma.';
        } else if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor.';
        } else {
          this.errorMessage = error.error?.message || 'Error al cambiar la contraseña. Inténtalo de nuevo.';
        }
      }
    });
  }

  /**
   * Volver atrás sin guardar cambios
   */
  cancel(): void {
    this.router.navigate(['/']);
  }
}
