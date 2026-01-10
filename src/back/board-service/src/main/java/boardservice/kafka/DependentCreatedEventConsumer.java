package boardservice.kafka;

import boardservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Consumidor de eventos Kafka para la creación automática de tableros
 * cuando se registra un nuevo usuario dependiente.
 */
@Service
@RequiredArgsConstructor
public class DependentCreatedEventConsumer {
    
    private final BoardService boardService;
    
    /**
     * Procesa el evento de creación de usuario dependiente.
     * Crea un nuevo tablero para el dependiente clonando el tablero público
     * del idioma especificado.
     *
     * @param event Evento con la información del nuevo dependiente
     */
    @KafkaListener(topics = "dependent.created", groupId = "${KAFKA_CONSUMER_GROUP:board-service-group}")
    public void handleDependentCreated(DependentCreatedEvent event) {
        try {
            UUID dependentId = UUID.fromString(event.getDependentId());
            String languageCode = event.getLanguageCode();
            
            boardService.createBoardForUser(dependentId, languageCode);
        } catch (Exception e) {
        }
    }
}
