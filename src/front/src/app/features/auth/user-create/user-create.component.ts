import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-user-create',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.css']
})
export class UserCreateComponent implements OnInit {
  userForm!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  successMessage = '';
  generatedPassword = '';
  showPassword = false;

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
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      language: ['es', Validators.required]
    });
  }

  get f() {
    return this.userForm.controls;
  }

  /**
   * Crear usuario final
   */
  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.generatedPassword = '';

    if (this.userForm.invalid) {
      return;
    }

    this.loading = true;

    this.userService.createUser(this.userForm.value).subscribe({
      next: (response) => {
        this.loading = false;
        this.generatedPassword = response.generatedPassword;
        this.successMessage = `Usuario "${response.user.name}" creado exitosamente.`;

        this.userForm.reset({ language: 'es' });
        this.submitted = false;
      },
      error: (error) => {
        this.loading = false;

        if (error.status === 403) {
          this.errorMessage = 'No tienes permisos para crear usuarios.';
        } else if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor.';
        } else {
          this.errorMessage = error.error?.message || 'Error al crear el usuario.';
        }
      }
    });
  }

  /**
   * Crear otro usuario
   */
  createAnother(): void {
    this.successMessage = '';
    this.generatedPassword = '';
    this.showPassword = false;
  }
}
