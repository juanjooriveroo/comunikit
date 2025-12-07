package boardservice.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import boardservice.events.StorageEventPublisher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private StorageEventPublisher storageEventPublisher;

    private UUID ownerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
    }

    @Test
    void publishStorageDelta_ShouldSendEventToCorrectTopic() {
        long deltaBytes = 1024L;

        storageEventPublisher.publishStorageDelta(ownerId, deltaBytes);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StorageUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(StorageUpdatedEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertEquals("storage.updated", topicCaptor.getValue());
        assertEquals(ownerId.toString(), keyCaptor.getValue());

        StorageUpdatedEvent capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals(ownerId, capturedEvent.getOwnerId());
        assertEquals(deltaBytes, capturedEvent.getDeltaBytes());
        assertNotNull(capturedEvent.getEventId());
    }

    @Test
    void publishStorageDelta_ShouldUseOwnerIdAsKey() {
        long deltaBytes = 2048L;

        storageEventPublisher.publishStorageDelta(ownerId, deltaBytes);

        verify(kafkaTemplate).send(eq("storage.updated"), eq(ownerId.toString()), any(StorageUpdatedEvent.class));
    }

    @Test
    void publishStorageDelta_ShouldHandleNegativeDelta() {
        long deltaBytes = -1024L;

        storageEventPublisher.publishStorageDelta(ownerId, deltaBytes);

        ArgumentCaptor<StorageUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(StorageUpdatedEvent.class);
        verify(kafkaTemplate).send(eq("storage.updated"), anyString(), eventCaptor.capture());

        assertEquals(-1024L, eventCaptor.getValue().getDeltaBytes());
    }
}
