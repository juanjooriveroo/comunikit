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

import java.net.URI;
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
            summary = "Creación de usuario dependiente",
            description = "Registra un usuario dependiente a la cuenta con la que se crea"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro exitoso, devuelve las nuevas credenciales creadas de acceso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Petición inválida por parámetros incorrectos o rol de usuario no correcto"
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
    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestHeader("X-User-ID") String userId, @Valid @RequestBody CreateUserRequestDto request) {
        CreateUserResponseDto credentials = authService.createUser(request, userId);
        return ResponseEntity.created(URI.create("/user/profile/" + credentials.idUser())).body(credentials);
    }
}