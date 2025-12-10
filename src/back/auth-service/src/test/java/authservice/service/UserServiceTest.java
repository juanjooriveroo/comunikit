package authservice.service;

import authservice.dto.StorageValidationResponseDto;
import authservice.entity.User;
import authservice.exception.StorageLimitExceededException;
import authservice.exception.UserNotFoundException;
import authservice.mapper.UserMapper;
import authservice.repository.LanguageRepository;
import authservice.repository.UserRelationRepository;
import authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRelationRepository userRelationRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .storage_used(10f)
                .build();

        ReflectionTestUtils.setField(userService, "storageLimitMb", 50f);
    }

    @Test
    void validateStorage_AllowsWhenUnderLimit() {
        long bytesToAdd = 5 * 1024 * 1024; // 5 MB
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        StorageValidationResponseDto result = userService.validateStorage(userId, bytesToAdd);

        assertNotNull(result);
        assertTrue(result.allowed());
        verify(userRepository).findById(userId);
    }

    @Test
    void validateStorage_ThrowsWhenExceedsLimit() {
        user.setStorage_used(48f);
        long bytesToAdd = 5 * 1024 * 1024; // 5 MB
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(StorageLimitExceededException.class, () -> userService.validateStorage(userId, bytesToAdd));
        verify(userRepository).findById(userId);
    }

    @Test
    void validateStorage_ThrowsWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.validateStorage(userId, 1024));
        verify(userRepository).findById(userId);
    }

    @Test
    void validateStorage_AllowsWhenStorageUsedIsNull() {
        user.setStorage_used(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        StorageValidationResponseDto result = userService.validateStorage(userId, 1024);

        assertNotNull(result);
        assertTrue(result.allowed());
        verify(userRepository).findById(userId);
    }
}
