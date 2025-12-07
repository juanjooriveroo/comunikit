package boardservice.kafka;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StorageUpdatedEvent {
    private UUID eventId;
    private UUID ownerId;
    private long deltaBytes;
}
