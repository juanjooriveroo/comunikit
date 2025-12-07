package boardservice.dto;

import boardservice.entity.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Schema(
        description = "Dto de imagen"
)
@Builder
public record ImageDto (
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
                description = "Contenido binario de la imagen.",
                nullable = false
        ) byte[] image,

        @Schema(
                description = "Idioma asociado a la imagen.",
                nullable = false
        ) Language language,

        @Schema(
                description = "MIME type original de la imagen.",
                example = "image/png",
                nullable = false
        ) String mimeType,

        @Schema(
                description = "Tamaño de la imagen en bytes tras la compresión.",
                example = "15324",
                nullable = false
        ) long sizeBytes,

        @Schema(
                description = "Si la imagen es pública o no.",
                example = "false",
                nullable = false
        ) boolean isPublic
){}