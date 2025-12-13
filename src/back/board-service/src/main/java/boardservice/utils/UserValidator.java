package boardservice.utils;

import boardservice.client.AuthServiceClient;
import boardservice.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Encapsula la validación entre usuarios mediante la petición de RestTemplate
 */
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final AuthServiceClient authServiceClient;

    public void validateUserAccess(String requestUserId, UUID ownerId) {
        if (requestUserId == null || requestUserId.isBlank()) {
            throw new UnauthorizedAccessException("Falta header X-User-ID en la petición");
        }

        if (ownerId == null) {
            throw new UnauthorizedAccessException("ownerId es requerido para validar la relación");
        }

        UUID requestUserUuid;
        try {
            requestUserUuid = UUID.fromString(requestUserId);
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedAccessException("Header X-User-ID inválido");
        }

        boolean hasAccess = authServiceClient.validateUserRelation(requestUserUuid, ownerId);

        if (!hasAccess) {
            throw new UnauthorizedAccessException("No tienes permiso para gestionar los recursos de este usuario");
        }
    }
}
