import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { ActivateComponent } from './features/auth/activate/activate.component';
import { RecoveryComponent } from './features/auth/recovery/recovery.component';
import { ResetPasswordComponent } from './features/auth/reset-password/reset-password.component';
import { UserCreateComponent } from './features/auth/user-create/user-create.component';
import { HomeComponent } from './features/home/home.component';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';
import { UserRole } from './shared/models/user.model';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'auth/activate/:id',
    component: ActivateComponent
  },
  {
    path: 'auth/recovery',
    component: RecoveryComponent
  },
  {
    path: 'auth/reset-password/:id',
    component: ResetPasswordComponent
  },
  {
    path: 'create-user',
    component: UserCreateComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.TUTOR, UserRole.ADMIN] }
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
