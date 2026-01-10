package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO para solicitar la posición de un pictograma en el tablero.
 */
@Schema(description = "Solicitud para posicionar un pictograma en el tablero")
public record PictogramPositionBoardRequestDto(
        @Schema(description = "ID del pictograma", example = "550e8400-e29b-41d4-a716-446655440000", nullable = false)
        UUID pictogramId,
        
        @Schema(description = "Columna en la cuadrícula (0-4)", example = "0", minimum = "0", maximum = "4", nullable = false)
        Integer col,
        
        @Schema(description = "Fila en la cuadrícula (0-5)", example = "0", minimum = "0", maximum = "5", nullable = false)
        Integer row
) {
}
