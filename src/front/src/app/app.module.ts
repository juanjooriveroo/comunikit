import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

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
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { DeleteAccountModalComponent } from './shared/components/delete-account-modal/delete-account-modal.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { DependentAccountsComponent } from './features/dependent-accounts/dependent-accounts.component';
import { DependentDetailComponent } from './features/dependent-detail/dependent-detail.component';
import { ConfirmDeleteModalComponent } from './shared/components/confirm-delete-modal/confirm-delete-modal.component';
import { GestorComponent } from './features/gestor/gestor.component';
import { ListaPictogramasComponent } from './features/gestor/pictogramas/lista-pictogramas/lista-pictogramas.component';
import { SelectorImagenesComponent } from './features/gestor/pictogramas/selector-imagenes/selector-imagenes.component';
import { ModalUploadImagenComponent } from './features/gestor/pictogramas/modal-upload-imagen/modal-upload-imagen.component';
import { CrearEditarPictogramaComponent } from './features/gestor/pictogramas/crear-editar-pictograma/crear-editar-pictograma.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    ActivateComponent,
    RecoveryComponent,
    ResetPasswordComponent,
    DeleteAccountComponent,
    EditProfileComponent,
    ChangePasswordComponent,
    UserCreateComponent,
    UserDependentProfileComponent,
    HomeComponent,
    NavbarComponent,
    DeleteAccountModalComponent,
    DependentAccountsComponent,
    DependentDetailComponent,
    ConfirmDeleteModalComponent,
    GestorComponent,
    ListaPictogramasComponent,
    SelectorImagenesComponent,
    ModalUploadImagenComponent,
    CrearEditarPictogramaComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    FontAwesomeModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
