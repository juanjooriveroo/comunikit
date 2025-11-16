import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  faEye = faEye;
  faEyeSlash = faEyeSlash;

  registerForm!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  showPassword = false;
  showConfirmPassword = false;
  successMessage = '';
  registrationSuccess = false;

  languages = [
    { code: 'es', name: 'Español' },
    { code: 'en', name: 'English' },
    { code: 'fr', name: 'Français' },
    { code: 'de', name: 'Deutsch' },
    { code: 'pt', name: 'Portugués'}
  ];

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

    this.registerForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      language: ['es', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  get f() {
    return this.registerForm.controls;
  }

  /**
   * Validador personalizado para verificar que las contraseñas coincidan
   */
  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
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

    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;

    const { confirmPassword, ...userData } = this.registerForm.value;

    const registrationData = {
      ...userData,
      rol: 'TUTOR'
    };

    this.authService.register(registrationData).subscribe({
      next: () => {
        this.successMessage = 'Registro exitoso. Revisa tu email para activar tu cuenta.';
        this.registrationSuccess = true;
        this.loading = false;
        this.registerForm.reset();
      },
      error: (error) => {
        this.loading = false;

        if (error.status === 409) {
          this.errorMessage = 'Este email ya está registrado. Intenta iniciar sesión o usa otro email.';
        } else if (error.status === 400) {
          this.errorMessage = 'Los datos ingresados no son válidos. Verifica los campos.';
        } else if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor. Verifica tu conexión.';
        } else {
          this.errorMessage = error.error?.message || 'Error al registrarse. Inténtalo de nuevo.';
        }
      }
    });
  }

  /**
   * Alternar visibilidad de contraseña
   */
  togglePasswordVisibility(field: 'password' | 'confirmPassword'): void {
    if (field === 'password') {
      this.showPassword = !this.showPassword;
    } else {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }
}
