package authservice.kafka;

import authservice.event.StorageUpdatedEvent;
import authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StorageEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(StorageEventConsumer.class);

    private final UserRepository userRepository;

    private float storageLimitMb = 50f;

    @KafkaListener(topics = "storage.updated", groupId = "${KAFKA_CONSUMER_GROUP:auth-service-test}")
    public void onStorageUpdated(StorageUpdatedEvent event) {
        userRepository.findById(event.getOwnerId()).ifPresent(user -> {
            float currentUsed = user.getStorage_used() == null ? 0F : user.getStorage_used();
            float deltaMb = bytesToMb(event.getDeltaBytes());
            float storageTotal = currentUsed + deltaMb;

            if (storageTotal < 0F) {
                storageTotal = 0F;
            }

            if (storageTotal > storageLimitMb) {
                log.warn("Storage usage {} MB exceeds limit {} MB for user {}", storageTotal, storageLimitMb, event.getOwnerId());
            }

            user.setStorage_used(storageTotal);
            userRepository.save(user);
        });
    }

    /**
     * Método para pasar de bytes a mb
     */
    private float bytesToMb(long bytes) {
        return bytes / 1024F / 1024F;
    }
}