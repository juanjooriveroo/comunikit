import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  faEye = faEye;
  faEyeSlash = faEyeSlash;

  resetForm!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  successMessage = '';
  resetSuccess = false;
  showPassword = false;
  showConfirmPassword = false;
  resetToken = '';
  userId = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/']);
      return;
    }

    this.resetToken = this.route.snapshot.paramMap.get('id') || '';

    if (!this.resetToken) {
      this.errorMessage = 'Token de recuperación inválido.';
      return;
    }

    this.resetForm = this.formBuilder.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  get f() {
    return this.resetForm.controls;
  }

  /**
   * Validador personalizado para verificar que las contraseñas coincidan
   */
  passwordMatchValidator(form: FormGroup) {
    const password = form.get('newPassword');
    const confirmPassword = form.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  /**
   * Manejar envío del formulario
   */
  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.resetForm.invalid) {
      return;
    }

    this.loading = true;

    const resetData = {
      userId: this.resetToken,
      newPassword: this.resetForm.value.newPassword
    };

    this.authService.confirmNewPassword(resetData).subscribe({
      next: () => {
        this.successMessage = 'Contraseña restablecida correctamente. Serás redirigido al login.';
        this.resetSuccess = true;
        this.loading = false;

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error: any) => {
        this.loading = false;

        if (error.status === 404) {
          this.errorMessage = 'Token de recuperación no válido o expirado.';
        } else if (error.status === 400) {
          this.errorMessage = 'La contraseña no cumple con los requisitos. Debe tener al menos 8 caracteres.';
        } else if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor. Verifica tu conexión.';
        } else {
          this.errorMessage = error.error?.message || 'Error al cambiar la contraseña. Inténtalo de nuevo.';
        }
      }
    });
  }

  /**
   * Alternar visibilidad de contraseña
   */
  togglePasswordVisibility(field: 'newPassword' | 'confirmPassword'): void {
    if (field === 'newPassword') {
      this.showPassword = !this.showPassword;
    } else {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }
}
