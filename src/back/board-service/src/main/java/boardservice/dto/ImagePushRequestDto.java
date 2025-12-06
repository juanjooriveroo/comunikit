package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ImagePushRequestDto (
        @Schema(
                description = "Nombre de la imagen.",
                example = "casa.png",
                nullable = false
        ) String name,

        @Schema(
                description = "Código de idioma asociado a la imagen.",
                nullable = false
        ) String language,

        @Schema(
                description = "Identificador del dueño de la imagen.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID ownerId
){}