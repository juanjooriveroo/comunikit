package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO simplificado de pictograma para respuestas de BoardFullDto.
 * Contiene solo la información necesaria para mostrar el pictograma.
 */
@Schema(description = "Pictograma simplificado para visualización")
public record PictogramSimpleDto(
        @Schema(description = "ID del pictograma", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
        
        @Schema(description = "Nombre del pictograma", example = "casa")
        String name,
        
        @Schema(description = "Imagen del pictograma")
        ImageSimpleDto image
) {
}
