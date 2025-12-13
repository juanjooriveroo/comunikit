package boardservice.dto;

import boardservice.entity.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Schema(
        description = "Dto de sección con grid fijo 5x6"
)
@Builder
public record SectionDto (
        @Schema(
                description = "Identificador único del pictograma.",
                example = "550e8400-e29b-41d4-a716-446655440002",
                nullable = false
        ) UUID id,

        @Schema(
                description = "Nombre único del pictograma.",
                example = "casa",
                nullable = false
        ) String name,

        @Schema(
                description = "Idioma de la sección.",
                nullable = false
        ) Language language,

        @Schema(
                description = "Sección asociada al pictograma.",
                nullable = false
        ) ImageDto image,

        @Schema(
                description = "Indica si la sección es pública o privada.",
                nullable = false
        ) Boolean isPublic,

        @Schema(
                description = "Pictogramas con sus posiciones en el grid 5x6 (col: 0-4, row: 0-5).",
                nullable = false
        ) List<PictogramPositionDto> pictograms
){}