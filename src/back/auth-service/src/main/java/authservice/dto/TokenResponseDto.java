package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Response de login que devuelve el token del usuario")
public record TokenResponseDto (
        @Schema(
                description = "Token del usuario con su UUID y sus roles",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwib...",
                format = "String",
                type = "String",
                nullable = false
        )
        String token
){}
