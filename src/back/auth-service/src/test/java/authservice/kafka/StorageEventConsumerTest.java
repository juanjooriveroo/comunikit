package authservice.kafka;

import authservice.entity.User;
import authservice.event.StorageUpdatedEvent;
import authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageEventConsumerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StorageEventConsumer storageEventConsumer;

    private UUID ownerId;
    private User testUser;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        testUser = User.builder()
                .id(ownerId)
                .email("test@example.com")
                .storage_used(10f)
                .build();

        ReflectionTestUtils.setField(storageEventConsumer, "storageLimitMb", 100f);
    }

    @Test
    void onStorageUpdated_ShouldIncreaseStorageUsed_WhenDeltaIsPositive() {
        long deltaBytes = 5 * 1024 * 1024; // 5 MB
        StorageUpdatedEvent event = new StorageUpdatedEvent(
                UUID.randomUUID(),
                ownerId,
                deltaBytes
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        storageEventConsumer.onStorageUpdated(event);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(15f, savedUser.getStorage_used(), 0.01); // 10 + 5 = 15 MB
    }

    @Test
    void onStorageUpdated_ShouldDecreaseStorageUsed_WhenDeltaIsNegative() {
        testUser.setStorage_used(20f);
        long deltaBytes = -5 * 1024 * 1024; // -5 MB
        StorageUpdatedEvent event = new StorageUpdatedEvent(
                UUID.randomUUID(),
                ownerId,
                deltaBytes
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        storageEventConsumer.onStorageUpdated(event);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(15f, savedUser.getStorage_used(), 0.01); // 20 - 5 = 15 MB
    }

    @Test
    void onStorageUpdated_ShouldNotGoNegative_WhenDeltaWouldMakeStorageNegative() {
        testUser.setStorage_used(3f);
        long deltaBytes = -5 * 1024 * 1024; // -5 MB (would result in -2)
        StorageUpdatedEvent event = new StorageUpdatedEvent(
                UUID.randomUUID(),
                ownerId,
                deltaBytes
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        storageEventConsumer.onStorageUpdated(event);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(0f, savedUser.getStorage_used(), 0.01); // Clamped to 0
    }

    @Test
    void onStorageUpdated_ShouldInitializeStorageUsed_WhenNull() {
        testUser.setStorage_used(null);
        long deltaBytes = 2 * 1024 * 1024; // 2 MB
        StorageUpdatedEvent event = new StorageUpdatedEvent(
                UUID.randomUUID(),
                ownerId,
                deltaBytes
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        storageEventConsumer.onStorageUpdated(event);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(2f, savedUser.getStorage_used(), 0.01); // 0 + 2 = 2 MB
    }

    @Test
    void onStorageUpdated_ShouldNotUpdate_WhenUserNotFound() {
        long deltaBytes = 1024;
        StorageUpdatedEvent event = new StorageUpdatedEvent(
                UUID.randomUUID(),
                ownerId,
                deltaBytes
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        storageEventConsumer.onStorageUpdated(event);

        verify(userRepository).findById(ownerId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void onStorageUpdated_ShouldLogWarning_WhenStorageExceedsLimit() {
        testUser.setStorage_used(95f);
        long deltaBytes = 10 * 1024 * 1024; // 10 MB (total will be 105 MB)
        StorageUpdatedEvent event = new StorageUpdatedEvent(
                UUID.randomUUID(),
                ownerId,
                deltaBytes
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        storageEventConsumer.onStorageUpdated(event);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(105f, savedUser.getStorage_used(), 0.01);
        // Warning is logged but update still happens
    }
}
