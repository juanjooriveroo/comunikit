package authservice.controller;

import authservice.dto.*;
import authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
        name = "Autentificación",
        description = "Endpoints para la autentificacion de usuarios"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Login de usuario",
            description = "Autentica un usuario y devuelve un token JWT"
    )
    @ApiResponses(value = {
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
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        TokenResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Registro de usuario",
            description = "Registra un usuario y devuelve un ok si se envió el correo de verificación"
    )
    @ApiResponses(value = {
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
    })
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
                            description = "Jugador no encontrado"
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
}