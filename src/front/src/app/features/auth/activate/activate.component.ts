import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-activate',
  templateUrl: './activate.component.html',
  styleUrls: ['./activate.component.css']
})
export class ActivateComponent implements OnInit {
  loading = true;
  successMessage = '';
  errorMessage = '';
  activationToken = '';

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.activationToken = this.route.snapshot.paramMap.get('id') || '';

    if (!this.activationToken) {
      this.errorMessage = 'Token de activación inválido.';
      this.loading = false;
      return;
    }

    this.activateAccount();
  }

  /**
   * Activar la cuenta con el token
   */
  private activateAccount(): void {
    this.authService.activateAccount(this.activationToken).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Cuenta activada correctamente. Serás redirigido al login en 3 segundos...';

        setTimeout(() => {
          this.router.navigate(['/login'], { queryParams: { activated: 'true' } });
        }, 3000);
      },
      error: (error) => {
        this.loading = false;

        if (error.status === 404) {
          this.errorMessage = 'Token de activación no válido o expirado.';
        } else if (error.status === 409) {
          this.errorMessage = 'Esta cuenta ya ha sido activada.';
        } else if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor. Verifica tu conexión.';
        } else {
          this.errorMessage = error.error?.message || 'Error al activar la cuenta. Inténtalo de nuevo.';
        }
      }
    });
  }

  /**
   * Reintentar la activación
   */
  retry(): void {
    this.loading = true;
    this.errorMessage = '';
    this.activateAccount();
  }

  /**
   * Ir a login
   */
  goToLogin(): void {
    this.router.navigate(['/login'], { queryParams: { activated: 'true' } });
  }
}
