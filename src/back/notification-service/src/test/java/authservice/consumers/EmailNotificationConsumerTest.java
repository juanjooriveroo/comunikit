package authservice.consumers;

import authservice.event.UserRecoveryAccountEvent;
import authservice.event.UserRegisteredEvent;
import authservice.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationConsumerTest {

    @Mock
    private EmailService emailService;

    private EmailNotificationConsumer emailNotificationConsumer;

    @BeforeEach
    void setUp() {
        emailNotificationConsumer = new EmailNotificationConsumer(emailService);
    }

    @Test
    void handleUserRegistered_Success() {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .email("test@example.com")
                .username("Test User")
                .userId("12345")
                .build();

        emailNotificationConsumer.handleUserRegistered(event);

        verify(emailService).sendVerificationEmail(
                "test@example.com",
                "Test User",
                "12345"
        );
    }

    @Test
    void handleUserRecovery_Success() {
        UserRecoveryAccountEvent event = UserRecoveryAccountEvent.builder()
                .email("test@example.com")
                .username("Test User")
                .userId("12345")
                .build();

        emailNotificationConsumer.handleUserRecovery(event);

        verify(emailService).sendActivationEmail(
                "test@example.com",
                "Test User",
                "12345"
        );
    }
}