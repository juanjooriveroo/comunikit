package boardservice.kafka;

import boardservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Consumidor de eventos Kafka para la creación automática de tableros
 * cuando se registra un nuevo usuario dependiente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
            
            log.info("Creando tablero para dependiente {} con idioma {}", dependentId, languageCode);
            boardService.createBoardForUser(dependentId, languageCode);
            log.info("Tablero creado exitosamente para dependiente {}", dependentId);
        } catch (Exception e) {
            log.error("Error al crear tablero para dependiente {}: {}", 
                    event.getDependentId(), e.getMessage(), e);
        }
    }
}
