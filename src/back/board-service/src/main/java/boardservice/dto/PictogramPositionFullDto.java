package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO que representa un pictograma posicionado en una sección para la vista de Play.
 * Usa PictogramSimpleDto que contiene solo la información necesaria para mostrar.
 */
@Schema(description = "Pictograma posicionado en una sección con información simplificada")
public record PictogramPositionFullDto(
        @Schema(description = "Columna en el grid (0-4)", example = "2")
        Integer col,

        @Schema(description = "Fila en el grid (0-5)", example = "3")
        Integer row,

        @Schema(description = "Pictograma simplificado en esta posición")
        PictogramSimpleDto pictogram
) {
}
