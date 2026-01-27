package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Request de login por username para usuarios con rol de user")
public record LoginByUsernameRequestDto(
        @Schema(description = "Nombre de acceso del usuario (username)", example = "frodriguezl01", nullable = false)
        @NotBlank(message = "El username no puede estar vacío")
        String username,

        @Schema(description = "Contraseña del usuario", example = "MiContraseñaSegura123", nullable = false)
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 8, message = "La contraseña tiene mínimo 8 caracteres")
        String password
) {}
