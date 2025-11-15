package authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Response de login que devuelve el token del usuario")
public record RegisterResponseDto (
        @Schema(
                description = "Valor de si se ha pedido enviado el email",
                example = "true",
                type = "Boolean",
                nullable = false
        )
        Boolean request
){}
