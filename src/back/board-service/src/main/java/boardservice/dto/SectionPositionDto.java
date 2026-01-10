package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO que representa una sección posicionada en el tablero.
 * Contiene la información de la sección y su posición en la cuadrícula 5x6.
 */
@Schema(description = "Sección posicionada en el tablero")
public record SectionPositionDto(
        @Schema(description = "ID de la sección", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectionId,
        
        @Schema(description = "Nombre de la sección", example = "Comida")
        String name,
        
        @Schema(description = "Columna en la cuadrícula (0-4)", example = "0", minimum = "0", maximum = "4")
        Integer col,
        
        @Schema(description = "Fila en la cuadrícula (0-5)", example = "0", minimum = "0", maximum = "5")
        Integer row,
        
        @Schema(description = "URL de la imagen en formato data URI (base64)")
        String imageUrl
) {
}
