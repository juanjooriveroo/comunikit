package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "Request para confirmar nueva contraseña en recuperación de cuenta")
public record ConfirmNewPasswordRequestDto (
        @Schema(
                description = "ID del usuario que está cambiando su contraseña",
                example = "123e4567-e89b-12d3-a456-426614174000",
                format = "UUID",
                type = "String",
                nullable = false
        )
        @NotNull(message = "El ID del usuario es obligatorio")
        UUID userId,

        @Schema(
                description = "Nueva contraseña del usuario. Debe tener al menos 8 caracteres",
                example = "NuevaPassword123",
                format = "String",
                type = "String",
                nullable = false
        )
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String newPassword
){}