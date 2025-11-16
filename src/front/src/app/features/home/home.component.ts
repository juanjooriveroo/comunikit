import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  isAuthenticated = false;
  currentUserName = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isAuthenticated = this.authService.isAuthenticated();
    const user = this.authService.getCurrentUser();
    if (user) {
      this.currentUserName = user.name;
    }
  }

  navigateToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
