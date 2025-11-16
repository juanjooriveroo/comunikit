import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-recovery',
  templateUrl: './recovery.component.html',
  styleUrls: ['./recovery.component.css']
})
export class RecoveryComponent implements OnInit {
  recoveryForm!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  successMessage = '';
  recoverySuccess = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/']);
      return;
    }

    this.recoveryForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  get f() {
    return this.recoveryForm.controls;
  }

  /**
   * Manejar envío del formulario
   */
  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.recoveryForm.invalid) {
      return;
    }

    this.loading = true;

    this.authService.recoveryAccount(this.recoveryForm.value.email).subscribe({
      next: () => {
        this.successMessage = 'Se ha enviado un correo de recuperación a tu email. Revisa tu bandeja de entrada.';
        this.recoverySuccess = true;
        this.loading = false;
        this.recoveryForm.reset();
      },
      error: (error: any) => {
        this.loading = false;

        if (error.status === 404) {
          this.errorMessage = 'No encontramos una cuenta con ese email.';
        } else if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor. Verifica tu conexión.';
        } else {
          this.errorMessage = error.error?.message || 'Error al solicitar recuperación. Inténtalo de nuevo.';
        }
      }
    });
  }
}
