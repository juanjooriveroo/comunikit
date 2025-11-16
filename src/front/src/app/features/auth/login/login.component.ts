import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  faEye = faEye;
  faEyeSlash = faEyeSlash;

  loginForm!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  returnUrl = '';
  showPassword = false;
  successMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/']);
      return;
    }

    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

    if (this.route.snapshot.queryParams['activated'] === 'true') {
      this.successMessage = 'Cuenta activada correctamente. Por favor, inicia sesión.';
    }

    if (this.route.snapshot.queryParams['expired'] === 'true') {
      this.errorMessage = 'Tu sesión ha expirado. Por favor, inicia sesión de nuevo.';
    }
  }

  // Getter para acceder fácilmente a los campos del formulario
  get f() {
    return this.loginForm.controls;
  }

  /**
   * Manejar envío del formulario
   */
  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.router.navigate([this.returnUrl]);
      },
      error: (error) => {
        this.loading = false;

        if (error.status === 401) {
          this.errorMessage = 'Email o contraseña incorrectos';
        } else if (error.status === 403) {
          this.errorMessage = 'Tu cuenta aún no ha sido activada. Revisa tu email.';
        } else if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor. Verifica tu conexión.';
        } else {
          this.errorMessage = error.error?.message || 'Error al iniciar sesión. Inténtalo de nuevo.';
        }
      }
    });
  }

  /**
   * Alternar visibilidad de contraseña
   */
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}
