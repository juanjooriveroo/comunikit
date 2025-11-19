import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { User, UserRole } from '../../models/user.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DeleteAccountModalComponent } from '../delete-account-modal/delete-account-modal.component';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isMenuOpen = false;
  private destroy$ = new Subject<void>();
  @ViewChild('deleteAccountModal') deleteAccountModal!: DeleteAccountModalComponent;

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  logout(): void {
    this.authService.logout();
    this.isMenuOpen = false;
  }

  /**
   * Abrir modal de solicitud de borrado de cuenta
   */
  openDeleteConfirmModal(): void {
    this.isMenuOpen = false;
    this.deleteAccountModal.openModal();
  }

  get isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  get canManageUsers(): boolean {
    return this.authService.canManageUsers();
  }

  get userInitials(): string {
    if (!this.currentUser?.name) return '?';
    return this.currentUser.name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }
}
