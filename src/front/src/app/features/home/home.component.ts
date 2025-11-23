import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { DependentService } from '../../core/services/dependent.service';
import { Router } from '@angular/router';
import { DependentAccount } from '../../shared/models/dependent.model';
import { UserRole } from '../../shared/models/user.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  isAuthenticated = false;
  currentUserName = '';
  isTutor = false;
  dependentAccounts: DependentAccount[] = [];
  loadingDependents = false;

  constructor(
    private authService: AuthService,
    private dependentService: DependentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isAuthenticated = this.authService.isAuthenticated();
    const user = this.authService.getCurrentUser();
    if (user) {
      this.currentUserName = user.name;
      this.isTutor = user.role === UserRole.TUTOR;

      if (this.isTutor) {
        this.loadDependentAccounts();
      }
    }
  }

  loadDependentAccounts(): void {
    this.loadingDependents = true;
    this.dependentService.getAllDependents().subscribe({
      next: (response) => {
        this.dependentAccounts = response.accounts;
        this.loadingDependents = false;
      },
      error: (error) => {
        console.error('Error al cargar cuentas dependientes:', error);
        this.loadingDependents = false;
      }
    });
  }

  navigateToDependentDetail(id: string): void {
    this.router.navigate(['/dependent-detail', id]);
  }

  navigateToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
