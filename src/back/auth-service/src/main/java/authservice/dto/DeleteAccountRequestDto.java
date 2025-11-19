package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Request para borrar una cuenta permanentemente")
public record DeleteAccountRequestDto(
        @Schema(
                description = "Contraseña del usuario. Debe tener al menos 8 caracteres",
                example = "MiContraseñaSegura123",
                type = "String",
                nullable = false
        )
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 8, message = "La contraseña tiene mínimo 8 caracteres")
        String password
) {}