package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record StorageValidationResponseDto(
        @Schema(
            description = "Indica si se permite almacenar", 
            example = "true"
        ) boolean allowed
) {}