import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit {
  editForm!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  successMessage = '';
  editSuccess = false;

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
    // Redirigir si no está autenticado
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    const currentUser = this.authService.getCurrentUser();

    this.editForm = this.formBuilder.group({
      name: [currentUser?.name || '', [Validators.required, Validators.minLength(3)]],
      email: [currentUser?.email || '', [Validators.required, Validators.email]],
      language: [currentUser?.language || 'es', Validators.required]
    });
  }

  get f() {
    return this.editForm.controls;
  }

  /**
   * Manejar envío del formulario
   */
  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.editForm.invalid) {
      return;
    }

    this.loading = true;

    const profileData = {
      name: this.editForm.value.name,
      email: this.editForm.value.email,
      language: this.editForm.value.language
    };

    this.authService.editProfile(profileData).subscribe({
      next: () => {
        this.successMessage = '✓ Perfil actualizado correctamente';
        this.editSuccess = true;
        this.loading = false;

        // Redirigir al home después de 2 segundos
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 2000);
      },
      error: (error) => {
        this.loading = false;

        if (error.status === 400) {
          this.errorMessage = error.error?.message || 'Los datos ingresados no son válidos.';
        } else if (error.status === 404) {
          this.errorMessage = 'Usuario no encontrado.';
        } else if (error.status === 409) {
          this.errorMessage = error.error?.message || 'El email ya está registrado.';
        } else if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor.';
        } else {
          this.errorMessage = error.error?.message || 'Error al actualizar el perfil. Inténtalo de nuevo.';
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
