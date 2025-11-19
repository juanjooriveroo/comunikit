import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-delete-account',
  templateUrl: './delete-account.component.html',
  styleUrls: ['./delete-account.component.css']
})
export class DeleteAccountComponent implements OnInit {
  faEye = faEye;
  faEyeSlash = faEyeSlash;

  deleteForm!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  successMessage = '';
  deleteSuccess = false;
  showPassword = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.deleteForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  get f() {
    return this.deleteForm.controls;
  }

  /**
   * Alternar visibilidad de contraseña
   */
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  /**
   * Manejar envío del formulario
   */
  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.deleteForm.invalid) {
      return;
    }

    this.loading = true;

    const deleteData = {
      password: this.deleteForm.value.password
    };

    this.authService.deleteAccount(deleteData).subscribe({
      next: () => {
        this.successMessage = '¡Hasta pronto! Tu cuenta ha sido eliminada correctamente.';
        this.deleteSuccess = true;
        this.loading = false;

        setTimeout(() => {
          localStorage.removeItem('token');
          this.authService.logout();
          this.router.navigate(['/']);
        }, 2000);
      },
      error: (error: any) => {
        this.loading = false;

        if (error.status === 401) {
          this.errorMessage = 'Contraseña incorrecta. Verifica e intenta de nuevo.';
          return;
        } else if (error.status === 404) {
          this.errorMessage = 'Usuario no encontrado.';
          return;
        } else if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor. Verifica tu conexión.';
          return;
        } else {
          this.errorMessage = error.error?.message || 'Error al eliminar la cuenta. Inténtalo de nuevo.';
          return;
        }
      }
    });
  }
}
