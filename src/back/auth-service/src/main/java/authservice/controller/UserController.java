package authservice.controller;

import authservice.dto.CreateUserRequestDto;
import authservice.dto.CreateUserResponseDto;
import authservice.dto.DependentAccountDto;
import authservice.dto.EditProfileRequestDto;
import authservice.dto.GetAllDependentsAccountsResponseDto;
import authservice.dto.StorageValidationRequestDto;
import authservice.dto.StorageValidationResponseDto;
import authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(
        name = "Usuarios",
        description = "Endpoints para la gestión de usuarios"
)
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Creación de usuario dependiente",
            description = "Registra un usuario dependiente a la cuenta con la que se crea",
            responses = {
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
            }
    )
    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestHeader("X-User-ID") String userId, @Valid @RequestBody CreateUserRequestDto request) {
        CreateUserResponseDto credentials = userService.createUser(request, userId);
        return ResponseEntity.created(URI.create("/user/profile/" + credentials.idUser())).body(credentials);
    }

    @Operation(
            summary = "Cambiar datos de una cuenta",
            description = "Cambia los datos de una cuenta y los guarda en la base de datos",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cambio de datos exitosos"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario o lenguaje no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PutMapping("/edit-profile")
    public ResponseEntity<?> editProfile(@RequestHeader("X-User-ID") String userId, @Valid @RequestBody EditProfileRequestDto request) {
        userService.editProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Obtener todas las cuentas dependientes",
            description = "Obtienes el uuid, nombre, nombre de usuario de los perfiles dependientes a tu cuenta",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Obtención exitósa"
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
    @GetMapping("/get-dependents-accounts")
    public ResponseEntity<?> getAllDependentsAccounts(@RequestHeader("X-User-ID") String userID) {
        GetAllDependentsAccountsResponseDto response = userService.getAllDependentsAccounts(userID);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtener la cuenta dependiente solicitada",
            description = "Obtienes la cuenta dependiente solicitada siempre y cuando tengas permiso para hacerlo",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Obtención exitósa"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuario no válido para la petición"
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
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getDependentAccount(@RequestHeader("X-User-ID") String userID, @PathVariable String id) {
        DependentAccountDto response = userService.getDependentAccount(userID, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Validar relación tutor-dependiente",
            description = "Valida si el tutorId tiene una relación con el dependentId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Validación exitosa, devuelve true si existe la relación"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    )
            }
    )
    @GetMapping("/validate-relation")
    public ResponseEntity<Boolean> validateRelation(
            @RequestParam UUID tutorId,
            @RequestParam UUID dependentId
    ) {
        boolean isValid = userService.validateUserRelation(tutorId, dependentId);
        return ResponseEntity.ok(isValid);
    }

    @Operation(
            summary = "Validar límite de almacenamiento",
            description = "Comprueba si el usuario puede almacenar los bytes solicitados sin exceder su límite",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Validación exitosa"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "El límite de almacenamiento sería superado"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    )
            }
    )
    @PostMapping("/storage/validate")
    public ResponseEntity<?> validateStorage(
            @RequestHeader("X-User-ID") String requesterId,
            @RequestBody StorageValidationRequestDto request
    ) {
        StorageValidationResponseDto response = userService.validateStorage(request.ownerId(), request.bytesToAdd());
        return ResponseEntity.ok(response);
    }
}
