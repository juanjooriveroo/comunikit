package authservice.kafka;

import authservice.event.UserRecoveryAccountEvent;
import authservice.event.UserRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaEventPublisher kafkaEventPublisher;

    @Captor
    private ArgumentCaptor<UserRegisteredEvent> userRegisteredEventCaptor;

    @Captor
    private ArgumentCaptor<UserRecoveryAccountEvent> userRecoveryEventCaptor;

    private UserRegisteredEvent userRegisteredEvent;
    private UserRecoveryAccountEvent userRecoveryEvent;

    @BeforeEach
    void setUp() {
        String userId = UUID.randomUUID().toString();

        userRegisteredEvent = UserRegisteredEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userId)
                .email("test@example.com")
                .username("Test User")
                .timestamp(LocalDateTime.now())
                .build();

        userRecoveryEvent = UserRecoveryAccountEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userId)
                .email("test@example.com")
                .username("Test User")
                .build();
    }

    @Test
    void publishUserRegistered_ShouldSendEventToCorrectTopic() {
        kafkaEventPublisher.publishUserRegistered(userRegisteredEvent);

        verify(kafkaTemplate).send("user.registered", userRegisteredEvent.getUserId(), userRegisteredEvent);
    }

    @Test
    void publishRecoveryAccount_ShouldSendEventToCorrectTopic() {
        kafkaEventPublisher.publishRecoveryAccount(userRecoveryEvent);

        verify(kafkaTemplate).send("user.recovery", userRecoveryEvent.getUserId(), userRecoveryEvent);
    }

    @Test
    void publishUserRegistered_ShouldUseUserIdAsKey() {
        kafkaEventPublisher.publishUserRegistered(userRegisteredEvent);

        verify(kafkaTemplate).send("user.registered", userRegisteredEvent.getUserId(), userRegisteredEvent);
    }

    @Test
    void publishRecoveryAccount_ShouldUseUserIdAsKey() {
        kafkaEventPublisher.publishRecoveryAccount(userRecoveryEvent);

        verify(kafkaTemplate).send("user.recovery", userRecoveryEvent.getUserId(), userRecoveryEvent);
    }
}