package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PictogramPushRequestDto (
        @Schema(
                description = "Identificador de la imagen.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID imageId,

        @Schema(
                description = "Identificador del dueño de la imagen.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID ownerId,

        @Schema(
                description = "Nombre del pictograma.",
                example = "casa",
                nullable = false
        ) String name,

        @Schema(
                description = "Código de idioma asociado a la imagen.",
                nullable = false
        ) String language
){}