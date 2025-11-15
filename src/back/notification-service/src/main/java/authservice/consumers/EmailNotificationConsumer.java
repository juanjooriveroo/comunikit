package authservice.consumers;

import authservice.event.UserRecoveryAccountEvent;
import authservice.event.UserRegisteredEvent;
import authservice.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Consumidor de eventos Kafka para procesar notificaciones por email
 */
@Service
@RequiredArgsConstructor
public class EmailNotificationConsumer {

    private final EmailService emailService;

    /**
     * Procesa evento de registro de usuario y envía email de verificación
     */
    @KafkaListener(topics = "user.registered", groupId = "notification-service-group")
    public void handleUserRegistered(UserRegisteredEvent event) {
        emailService.sendVerificationEmail(
                event.getEmail(),
                event.getUsername(),
                event.getUserId()
        );
    }

    /**
     * Procesa evento de recuperación de cuenta y envía email de activación
     */
    @KafkaListener(topics = "user.recovery", groupId = "notification-service-group")
    public void handleUserRecovery(UserRecoveryAccountEvent event) {
        emailService.sendActivationEmail(
                event.getEmail(),
                event.getUsername(),
                event.getUserId()
        );
    }
}