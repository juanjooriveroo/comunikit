package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

/**
 * DTO que representa un tablero completo con sus secciones y pictogramas posicionados.
 */
@Schema(description = "Tablero de comunicación con secciones y pictogramas posicionados en una cuadrícula 5x6")
public record BoardDto(
        @Schema(description = "ID único del tablero", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
        
        @Schema(description = "ID del usuario propietario (dependiente)", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID ownerId,
        
        @Schema(description = "Código del idioma del tablero", example = "es")
        String languageCode,
        
        @Schema(description = "Indica si es un tablero público (plantilla)", example = "false")
        boolean isPublic,
        
        @Schema(description = "Lista de secciones posicionadas en el tablero")
        List<SectionPositionDto> sections,
        
        @Schema(description = "Lista de pictogramas posicionados directamente en el tablero")
        List<PictogramPositionBoardDto> pictograms
) {
}
