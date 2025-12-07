package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ImageUpdateRequestDto(
        @Schema(
                description = "Identificador del dueño de la imagen.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID ownerId,

        @Schema(
                description = "Nombre nuevo de la imagen.",
                example = "casa-actualizada.png",
                nullable = true
        ) String name,

        @Schema(
                description = "Código de idioma asociado a la imagen.",
                nullable = true
        ) String language,

        @Schema(
                description = "Marcar la imagen como pública o privada.",
                example = "false",
                nullable = true
        ) Boolean isPublic
) {}
