package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Request para cambiar contraseña verificando la anterior")
public record ChangePasswordRequestDto (
        @Schema(
                description = "Contraseña anterior del usuario. Debe ser correcta para poder cambiar",
                example = "OldPassword123",
                format = "String",
                type = "String",
                nullable = false
        )
        @NotBlank(message = "La contraseña anterior es obligatoria")
        String oldPassword,

        @Schema(
                description = "Nueva contraseña del usuario. Debe tener al menos 8 caracteres",
                example = "NuevaPassword123",
                format = "String",
                type = "String",
                nullable = false
        )
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String newPassword,

        @Schema(
                description = "UUID del usuario a editar su cuenta si procede",
                example = "a81bc81b-dead-4e5d-abff-90865d1e13b1",
                type = "UUID",
                nullable = true
        )
        UUID userId
) {}
