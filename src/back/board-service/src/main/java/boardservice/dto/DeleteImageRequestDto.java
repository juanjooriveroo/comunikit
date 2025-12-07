package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteImageRequestDto(
        @Schema(
                description = "Identificador del dueño de la imagen.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID ownerId
) {}
