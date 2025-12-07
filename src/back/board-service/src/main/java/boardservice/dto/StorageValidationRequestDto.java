package boardservice.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record StorageValidationRequestDto(
        @Schema(
                description = "Identificador del dueño de la imagen.",
                example = "550e8400-e29b-41d4-a716-446655440003",
                nullable = false
        ) UUID ownerId,

        @Schema(
                description = "Bytes que añadir a la cuenta.",
                example = "abc78gsdbduoi2n2...",
                nullable = false
        )
        long bytesToAdd
) {}