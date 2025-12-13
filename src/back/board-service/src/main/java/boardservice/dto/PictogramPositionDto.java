package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para un pictograma con su posición en el grid 5x6")
public record PictogramPositionDto(
        @Schema(description = "Columna en el grid (0-4)", example = "2")
        Integer col,

        @Schema(description = "Fila en el grid (0-5)", example = "3")
        Integer row,

        @Schema(description = "Pictograma en esta posición")
        PictogramDto pictogram
) {}
