package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record GetAllPictogramsRequestDto (
        @Schema(
                description = "Identificador del dueño.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID ownerId
){}