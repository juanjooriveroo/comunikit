package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO de solicitud para posicionar una sección en el tablero.
 */
@Schema(description = "Solicitud para posicionar una sección en el tablero")
public record SectionPositionRequestDto(
        @Schema(description = "ID de la sección a posicionar", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "El ID de la sección es obligatorio")
        UUID sectionId,
        
        @Schema(description = "Columna en la cuadrícula (0-4)", example = "0", minimum = "0", maximum = "4")
        @NotNull(message = "La columna es obligatoria")
        @Min(value = 0, message = "La columna mínima es 0")
        @Max(value = 4, message = "La columna máxima es 4")
        Integer col,
        
        @Schema(description = "Fila en la cuadrícula (0-5)", example = "0", minimum = "0", maximum = "5")
        @NotNull(message = "La fila es obligatoria")
        @Min(value = 0, message = "La fila mínima es 0")
        @Max(value = 5, message = "La fila máxima es 5")
        Integer row
) {
}
