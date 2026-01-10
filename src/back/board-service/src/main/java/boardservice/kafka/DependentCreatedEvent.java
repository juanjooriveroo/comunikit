package boardservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento recibido cuando se crea un usuario dependiente en auth-service.
 * Board-service lo consume para crear el tablero del dependiente.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DependentCreatedEvent {
    private String eventId;
    private String dependentId;
    private String tutorId;
    private String languageCode;
    private LocalDateTime timestamp;
}
