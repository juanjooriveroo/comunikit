import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';

import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-user-create',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.css']
})
export class UserCreateComponent implements OnInit {
  faEye = faEye;
  faEyeSlash = faEyeSlash;

  userForm!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  showPassword = false;
  showConfirmPassword = false;

  languages = [
    { code: 'es', name: 'Español' },
    { code: 'en', name: 'English' },
    { code: 'fr', name: 'Français' },
    { code: 'de', name: 'Deutsch' },
    { code: 'pt', name: 'Portugués'}
  ];

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Verificar que solo un TUTOR puede acceder
    if (!this.authService.hasRole(this.authService.getCurrentUser()?.role as any)) {
      this.router.navigate(['/']);
      return;
    }

    this.userForm = this.formBuilder.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
      language: ['es', Validators.required]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  get f() {
    return this.userForm.controls;
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
   * Crear usuario dependiente
   */
  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';

    if (this.userForm.invalid) {
      return;
    }

    this.loading = true;

    const { firstName, lastName, password, language } = this.userForm.value;
    const fullName = `${firstName} ${lastName}`;

    const createUserRequest = {
      name: fullName,
      language: language,
      password: password,
      userId: null
    };

    this.userService.createUser(createUserRequest).subscribe({
      next: (response) => {
        // Redirigir inmediatamente al perfil del usuario dependiente
        this.router.navigate(['/user/profile', response.idUser]);
      },
      error: (error) => {
        this.loading = false;

        if (error.status === 403) {
          this.errorMessage = 'No tienes permisos para crear usuarios. Solo los tutores pueden hacerlo.';
        } else if (error.status === 400) {
          this.errorMessage = error.error?.message || 'Parámetros inválidos. Por favor, revisa los datos.';
        } else if (error.status === 409) {
          this.errorMessage = error.error?.message || 'Error de conflicto al crear el usuario.';
        } else if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor.';
        } else {
          this.errorMessage = error.error?.message || 'Error al crear el usuario dependiente.';
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
