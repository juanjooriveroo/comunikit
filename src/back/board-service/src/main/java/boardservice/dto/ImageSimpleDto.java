package boardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO simplificado de imagen para respuestas que solo necesitan ID y URL.
 * Se usa en el BoardFullDto para mostrar pictogramas.
 */
@Schema(description = "Imagen simplificada con ID y URL")
public record ImageSimpleDto(
        @Schema(description = "ID de la imagen", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
        
        @Schema(description = "URL de la imagen en formato data URI (base64)")
        String imageUrl
) {
}
