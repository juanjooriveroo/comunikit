package authservice.kafka;

import authservice.event.DependentCreatedEvent;
import authservice.event.UserDeleteRequestEvent;
import authservice.event.UserRecoveryAccountEvent;
import authservice.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publicador de eventos Kafka para notificaciones
 */
@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publica evento de registro de usuario
     */
    public void publishUserRegistered(UserRegisteredEvent event) {
        kafkaTemplate.send("user.registered", event.getUserId(), event);
    }

    /**
     * Publica evento de recuperación de cuenta
     */
    public void publishRecoveryAccount(UserRecoveryAccountEvent event) {
        kafkaTemplate.send("user.recovery", event.getUserId(), event);
    }

    /**
     * Publica evento de petición de baja
     */
    public void publishDeleteRequest(UserDeleteRequestEvent event) {
        kafkaTemplate.send("user.delete", event.getUserId(), event);
    }
    
    /**
     * Publica evento de creación de usuario dependiente.
     * Board-service consume este evento para crear el tablero del dependiente.
     */
    public void publishDependentCreated(DependentCreatedEvent event) {
        kafkaTemplate.send("dependent.created", event.getDependentId(), event);
    }
}