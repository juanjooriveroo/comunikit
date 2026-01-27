package authservice.controller;

import authservice.dto.ChangePasswordRequestDto;
import authservice.dto.ConfirmNewPasswordRequestDto;
import authservice.dto.DeleteAccountRequestDto;
import authservice.dto.LoginRequestDto;
import authservice.dto.RecoveryAccountRequestDto;
import authservice.dto.RegisterRequestDto;
import authservice.dto.RegisterResponseDto;
import authservice.dto.TokenResponseDto;
import authservice.dto.LoginByUsernameRequestDto;
import authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(
        name = "Autentificación",
        description = "Endpoints para la autentificacion de usuarios"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Login de usuario",
            description = "Autentica un usuario y devuelve un token JWT",
            responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Logueo exitoso, devuelve token"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autenticado o credenciales inválidas"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        TokenResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

        @Operation(
                        summary = "Login de usuario por username (rol USER)",
                        description = "Autentica un usuario por su username y sólo emite token si su rol es de tipo user",
                        responses = {
                                        @ApiResponse(responseCode = "200", description = "Logueo exitoso, devuelve token"),
                                        @ApiResponse(responseCode = "401", description = "No autenticado o credenciales inválidas"),
                                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                        }
        )
        @PostMapping("/login/user")
        public ResponseEntity<?> loginByUsername(@Valid @RequestBody LoginByUsernameRequestDto request) {
                TokenResponseDto response = authService.loginByUsername(request);
                return ResponseEntity.ok(response);
        }

    @Operation(
            summary = "Registro de usuario",
            description = "Registra un usuario y devuelve un ok si se envió el correo de verificación",
            responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Registro exitoso, devuelve true si se envió email de confirmación"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflicto de datos (duplicados, estado inválido, etc.)"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request) {
        RegisterResponseDto response = authService.register(request);
        if (response.request()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Activar cuenta",
            description = "Activa una cuenta por su id.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logueo exitoso"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PostMapping("/activate/{id}")
    public ResponseEntity<?> activate(@PathVariable UUID id){
        TokenResponseDto response = authService.activate(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Recuperar cuenta",
            description = "Recupera una cuenta por correo. Usa Kafka para enviar la peticion de correo de recuperación",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Envío de email exitoso"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PostMapping("/recovery-account")
    public ResponseEntity<?> recoveryAccount(@Valid @RequestBody RecoveryAccountRequestDto request){
        authService.recoveryAccount(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Confirmar nueva contraseña",
            description = "Recibe por request el UUID del usuario y cambia su contraseña a la indicada",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cambio exitoso"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PostMapping("/confirm-new-password")
    public ResponseEntity<?> confirmNewPassword(@Valid @RequestBody ConfirmNewPasswordRequestDto request){
        authService.confirmNewPassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Inicia petición de borrado de cuenta",
            description = "Recibe por token el UUID del usuario y tramita un correo de baja de usuario de forma asíncrona",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Email enviado / en curso"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PostMapping("delete-request")
    public ResponseEntity<?> deleteRequest(@RequestHeader("X-User-ID") String userId){
        authService.deleteRequest(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Inicia borrado de cuenta",
            description = "Recibe por token el UUID del usuario y tramita la baja de la cuenta",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cuenta eliminada con éxito"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestHeader("X-User-ID") String userId, @Valid @RequestBody DeleteAccountRequestDto request){
        authService.deleteAccount(request, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Cambiar contraseña",
            description = "Cambia la contraseña del usuario verificando que la anterior sea correcta",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cambio de contraseña exitoso"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Contraseña anterior incorrecta"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("X-User-ID") String userId, @Valid @RequestBody ChangePasswordRequestDto request) {
        authService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }
}