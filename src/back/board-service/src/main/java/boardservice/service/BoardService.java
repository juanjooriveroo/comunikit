package boardservice.service;

import boardservice.client.AuthServiceClient;
import boardservice.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final AuthServiceClient authServiceClient;

    public void validateUserAccess(String requestUserId, UUID ownerId) {
        UUID requestUserUuid = UUID.fromString(requestUserId);

        boolean hasAccess = authServiceClient.validateUserRelation(requestUserUuid, ownerId);

        if (!hasAccess) {
            throw new UnauthorizedAccessException("No tienes permiso para gestionar los recursos de este usuario");
        }
    }
}
