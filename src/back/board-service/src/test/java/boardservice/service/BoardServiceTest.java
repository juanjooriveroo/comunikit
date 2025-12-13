package boardservice.service;

import boardservice.client.AuthServiceClient;
import boardservice.exception.UnauthorizedAccessException;
import boardservice.utils.UserValidator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateUserAccess_AllowsWhenRelated() {
        UUID requester = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        when(authServiceClient.validateUserRelation(requester, owner)).thenReturn(true);

        assertDoesNotThrow(() -> userValidator.validateUserAccess(requester.toString(), owner));
    }

    @Test
    void validateUserAccess_ThrowsWhenNotRelated() {
        UUID requester = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        when(authServiceClient.validateUserRelation(requester, owner)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () -> userValidator.validateUserAccess(requester.toString(), owner));
    }
}
