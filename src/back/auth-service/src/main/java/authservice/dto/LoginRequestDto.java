package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Request de login para acceder a un usuario")
public record LoginRequestDto (
        @Schema(
                description = "Correo electrónico del usuario. Debe tener un formato válido de email",
                example = "juanjo@example.com",
                format = "String",
                type = "String",
                nullable = false
        )
        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "El correo no tiene la sintaxis de un email")
        String email,

        @Schema(
                description = "Contraseña del usuario. Debe tener al menos 8 caracteres",
                example = "MiContraseñaSegura123",
                type = "String",
                nullable = false
        )
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 8, message = "La contraseña tiene mínimo 8 caracteres")
        String password
){}
