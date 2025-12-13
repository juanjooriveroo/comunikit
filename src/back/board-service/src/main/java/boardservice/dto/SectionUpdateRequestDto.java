package boardservice.dto;

import java.util.UUID;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record SectionUpdateRequestDto(
    @Schema(
                description = "Identificador del dueño del pictograma.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID ownerId,

        @Schema(
                description = "Identificador de la imagen a asociar.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID imageId,

        @Schema(
                description = "Nombre nuevo del pictograma.",
                example = "casa",
                nullable = false
        ) String name,

        @Schema(
                description = "Código de idioma asociado al pictograma.",
                nullable = false
        ) String language,

        @Schema(
                description = "Lista de UUIDs de pictogramas.",
                nullable = false
        ) List<UUID> pictograms
) {}