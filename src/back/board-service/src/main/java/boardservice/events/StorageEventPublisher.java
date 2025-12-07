package boardservice.events;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import boardservice.kafka.StorageUpdatedEvent;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StorageEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "storage.updated";

    public void publishStorageDelta(UUID ownerId, long deltaBytes) {
        StorageUpdatedEvent event = StorageUpdatedEvent.builder()
                .eventId(UUID.randomUUID())
                .ownerId(ownerId)
                .deltaBytes(deltaBytes)
                .build();

        kafkaTemplate.send(TOPIC, ownerId.toString(), event);
    }
}
