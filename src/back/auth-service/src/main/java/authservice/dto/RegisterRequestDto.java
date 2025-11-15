package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Request de register para crear un usuario")
public record RegisterRequestDto (
        @Schema(
                description = "Nombre de usuario",
                example = "juanjoDev",
                format = "String",
                type = "String",
                nullable = false
        )
        @NotBlank(message = "El nombre no puede estar vacío")
        String name,

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
        String password,

        @Schema(
                description = "Rol asignado al usuario.",
                example = "USER",
                type = "String",
                nullable = false
        )
        @NotNull(message = "El rol es obligatorio")
        String rol,

        @Schema(
                description = "Idioma preferido del usuario.",
                example = "es",
                type = "String",
                nullable = false
        )
        @NotNull(message = "El idioma es obligatorio")
        String language
){}