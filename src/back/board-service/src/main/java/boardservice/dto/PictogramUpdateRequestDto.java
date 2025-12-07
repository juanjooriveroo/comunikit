package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PictogramUpdateRequestDto(
        @Schema(
                description = "Identificador del dueño del pictograma.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID ownerId,

        @Schema(
                description = "Identificador de la imagen a asociar.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = true
        ) UUID imageId,

        @Schema(
                description = "Nombre nuevo del pictograma.",
                example = "casa",
                nullable = true
        ) String name,

        @Schema(
                description = "Código de idioma asociado al pictograma.",
                nullable = true
        ) String language
) {}
