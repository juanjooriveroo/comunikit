package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record StorageValidationRequestDto(
        @Schema(
            description = "Usuario dueño del recurso", 
            example = "550e8400-e29b-41d4-a716-446655440003"
        ) UUID ownerId,

        @Schema(
            description = "Bytes adicionales que se quieren almacenar", 
            example = "10240"
        ) long bytesToAdd
) {}