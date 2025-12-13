package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SectionPushRequestDto (
        @Schema(
                description = "Identificador de la imagen de portada de la sección.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID imageId,

        @Schema(
                description = "Identificador del dueño de la sección.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID ownerId,

        @Schema(
                description = "Nombre de la sección.",
                example = "casa",
                nullable = false
        ) String name,

        @Schema(
                description = "Código de idioma asociado a la sección.",
                nullable = false
        ) String language
){}