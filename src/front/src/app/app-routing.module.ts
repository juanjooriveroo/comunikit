import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { ActivateComponent } from './features/auth/activate/activate.component';
import { RecoveryComponent } from './features/auth/recovery/recovery.component';
import { ResetPasswordComponent } from './features/auth/reset-password/reset-password.component';
import { DeleteAccountComponent } from './features/auth/delete-account/delete-account.component';
import { EditProfileComponent } from './features/auth/edit-profile/edit-profile.component';
import { ChangePasswordComponent } from './features/auth/change-password/change-password.component';
import { UserCreateComponent } from './features/auth/user-create/user-create.component';
import { UserDependentProfileComponent } from './features/auth/user/dependent/profile/profile.component';
import { HomeComponent } from './features/home/home.component';
import { DependentDetailComponent } from './features/dependent-detail/dependent-detail.component';
import { GestorComponent } from './features/gestor/gestor.component';
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
    path: 'auth/delete-account',
    component: DeleteAccountComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'auth/edit-profile',
    component: EditProfileComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'auth/change-password',
    component: ChangePasswordComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'create-user',
    component: UserCreateComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.TUTOR] }
  },
  {
    path: 'user/profile/:id',
    component: UserDependentProfileComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'dependent-detail/:id',
    component: DependentDetailComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.TUTOR] }
  },
  {
    path: 'gestor/:id',
    component: GestorComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.TUTOR] }
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
