package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

/**
 * DTO que representa una sección posicionada en el tablero con todos sus pictogramas incluidos.
 * Se usa para el endpoint público que necesita mostrar los pictogramas de cada sección.
 */
@Schema(description = "Sección posicionada en el tablero con pictogramas incluidos")
public record SectionPositionFullDto(
        @Schema(description = "ID de la sección", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectionId,
        
        @Schema(description = "Nombre de la sección", example = "Comida")
        String name,
        
        @Schema(description = "Columna en la cuadrícula (0-4)", example = "0", minimum = "0", maximum = "4")
        Integer col,
        
        @Schema(description = "Fila en la cuadrícula (0-5)", example = "0", minimum = "0", maximum = "5")
        Integer row,
        
        @Schema(description = "URL de la imagen en formato data URI (base64)")
        String imageUrl,
        
        @Schema(description = "Lista de pictogramas posicionados en la sección")
        List<PictogramPositionFullDto> pictograms
) {
}
