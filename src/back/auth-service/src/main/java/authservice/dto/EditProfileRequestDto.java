package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

import java.util.UUID;

public record EditProfileRequestDto (
        @Schema(
                description = "Nombre completo del usuario",
                example = "Marcos Pérez Manzano",
                format = "String",
                type = "String",
                nullable = false
        )
        String name,

        @Schema(
                description = "Correo electrónico del usuario. Debe tener un formato válido de email",
                example = "juanjo@example.com",
                format = "String",
                type = "String",
                nullable = false
        )
        @Email(message = "El correo no tiene la sintaxis de un email")
        String email,

        @Schema(
                description = "Idioma preferido del usuario.",
                example = "es",
                type = "String",
                nullable = false
        )
        String language,

        @Schema(
                description = "UUID del usuario a editar su cuenta si procede",
                example = "a81bc81b-dead-4e5d-abff-90865d1e13b1",
                type = "UUID",
                nullable = true
        )
        UUID userId
){}
