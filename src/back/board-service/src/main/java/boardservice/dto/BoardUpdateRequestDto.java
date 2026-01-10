package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO de solicitud para actualizar las secciones y pictogramas de un tablero.
 * Reemplaza todas las posiciones existentes.
 */
@Schema(description = "Solicitud para actualizar las secciones y pictogramas de un tablero")
public record BoardUpdateRequestDto(
        @Schema(description = "Lista de secciones con sus posiciones en la cuadrícula 5x6")
        @NotNull(message = "La lista de secciones es obligatoria")
        @Valid
        List<SectionPositionRequestDto> sections,
        
        @Schema(description = "Lista de pictogramas con sus posiciones en la cuadrícula 5x6")
        @Valid
        List<PictogramPositionBoardRequestDto> pictograms
) {
}
