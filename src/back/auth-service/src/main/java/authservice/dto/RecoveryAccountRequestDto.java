package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "Request para solicitar recuperación de cuenta")
public record RecoveryAccountRequestDto(
        @Schema(
                description = "Email del usuario para recuperar la cuenta",
                example = "usuario@example.com",
                format = "String",
                type = "String",
                nullable = false
        )
        @NotBlank(message = "Las credenciales son obligatorias")
        String email
){}