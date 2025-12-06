package boardservice.dto;

import boardservice.entity.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Schema(
        description = "Dto de pictograma"
)
@Builder
public record PictogramDto (

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
                description = "Idioma del pictograma.",
                nullable = false
        ) Language language,

        @Schema(
                description = "Imagen asociada al pictograma.",
                nullable = false
        ) ImageDto image
){}