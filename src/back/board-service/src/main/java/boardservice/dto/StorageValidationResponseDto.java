package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record StorageValidationResponseDto(
        @Schema(
                description = "Indica si se puede almacenar la imagen sin superar el límite",
                nullable = false
        ) boolean allowed,

        @Schema(
                description = "Límite máximo en MB",
                nullable = false
        ) float limitMb,

        @Schema(
                description = "Almacenamiento utilizado en MB tras la operación",
                nullable = false
        ) float usedMb,

        @Schema(
                description = "Almacenamiento restante en MB",
                nullable = false
        ) float remainingMb
) {}
