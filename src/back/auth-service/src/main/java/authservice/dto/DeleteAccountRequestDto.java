package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

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
        String password,

        @Schema(
                description = "UUID del usuario a editar su cuenta si procede",
                example = "a81bc81b-dead-4e5d-abff-90865d1e13b1",
                type = "UUID",
                nullable = true
        )
        UUID userId
) {}