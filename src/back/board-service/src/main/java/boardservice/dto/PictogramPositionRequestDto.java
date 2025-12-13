package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO para añadir/actualizar un pictograma en una posición del grid 5x6")
public record PictogramPositionRequestDto(
        @Schema(description = "ID del pictograma", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID pictogramId,

        @Schema(description = "Columna en el grid (0-4)", example = "2", minimum = "0", maximum = "4")
        Integer col,

        @Schema(description = "Fila en el grid (0-5)", example = "3", minimum = "0", maximum = "5")
        Integer row
) {}
