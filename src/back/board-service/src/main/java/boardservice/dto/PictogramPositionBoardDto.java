package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO que representa un pictograma posicionado directamente en el tablero.
 * Similar a SectionPositionDto pero para pictogramas sueltos.
 */
@Schema(description = "Pictograma posicionado en el tablero")
public record PictogramPositionBoardDto(
        @Schema(description = "ID del pictograma", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID pictogramId,
        
        @Schema(description = "Nombre del pictograma", example = "Agua")
        String name,
        
        @Schema(description = "Columna en la cuadrícula (0-4)", example = "0", minimum = "0", maximum = "4")
        Integer col,
        
        @Schema(description = "Fila en la cuadrícula (0-5)", example = "0", minimum = "0", maximum = "5")
        Integer row,
        
        @Schema(description = "URL de la imagen en formato data URI (base64)")
        String imageUrl
) {
}
