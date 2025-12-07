package boardservice.client;

import boardservice.dto.StorageValidationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthServiceClient authServiceClient;

    private final String authServiceUrl = "http://localhost:8081/auth";
    private UUID tutorId;
    private UUID dependentId;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authServiceClient, "authServiceUrl", authServiceUrl);
        tutorId = UUID.randomUUID();
        dependentId = UUID.randomUUID();
    }

    @Test
    void validateUserRelation_ShouldReturnTrue_WhenRelationExists() {
        String expectedUrl = String.format("%s/validate-relation?tutorId=%s&dependentId=%s",
                authServiceUrl, tutorId, dependentId);

        when(restTemplate.getForObject(expectedUrl, Boolean.class)).thenReturn(true);

        boolean result = authServiceClient.validateUserRelation(tutorId, dependentId);

        assertTrue(result);
        verify(restTemplate).getForObject(expectedUrl, Boolean.class);
    }

    @Test
    void validateUserRelation_ShouldReturnFalse_WhenRelationDoesNotExist() {
        String expectedUrl = String.format("%s/validate-relation?tutorId=%s&dependentId=%s",
                authServiceUrl, tutorId, dependentId);

        when(restTemplate.getForObject(expectedUrl, Boolean.class)).thenReturn(false);

        boolean result = authServiceClient.validateUserRelation(tutorId, dependentId);

        assertFalse(result);
        verify(restTemplate).getForObject(expectedUrl, Boolean.class);
    }

    @Test
    void validateUserRelation_ShouldReturnFalse_WhenExceptionOccurs() {
        String expectedUrl = String.format("%s/validate-relation?tutorId=%s&dependentId=%s",
                authServiceUrl, tutorId, dependentId);

        when(restTemplate.getForObject(expectedUrl, Boolean.class))
                .thenThrow(new RuntimeException("Connection error"));

        boolean result = authServiceClient.validateUserRelation(tutorId, dependentId);

        assertFalse(result);
        verify(restTemplate).getForObject(expectedUrl, Boolean.class);
    }

    @Test
    void validateStorageLimit_ShouldReturnResponse_WhenLimitNotExceeded() {
        UUID requesterId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        long bytesToAdd = 1024L;
        String expectedUrl = String.format("%s/storage/validate", authServiceUrl);

        StorageValidationResponseDto expectedResponse = new StorageValidationResponseDto(
                true, 100f, 10f, 90f
        );

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(StorageValidationResponseDto.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        StorageValidationResponseDto result = authServiceClient.validateStorageLimit(requesterId, ownerId, bytesToAdd);

        assertNotNull(result);
        assertTrue(result.allowed());
        assertEquals(100f, result.limitMb());
        assertEquals(10f, result.usedMb());
        assertEquals(90f, result.remainingMb());
    }

    @Test
    void validateStorageLimit_ShouldThrowException_WhenLimitExceeded() {
        UUID requesterId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        long bytesToAdd = 1024L;
        String expectedUrl = String.format("%s/storage/validate", authServiceUrl);

        StorageValidationResponseDto expectedResponse = new StorageValidationResponseDto(
                false, 100f, 101f, -1f
        );

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(StorageValidationResponseDto.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        assertThrows(RuntimeException.class, () ->
                authServiceClient.validateStorageLimit(requesterId, ownerId, bytesToAdd)
        );
    }

    @Test
    void validateStorageLimit_ShouldIncludeCorrectHeaders() {
        UUID requesterId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        long bytesToAdd = 1024L;

        StorageValidationResponseDto expectedResponse = new StorageValidationResponseDto(
                true, 100f, 10f, 90f
        );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(StorageValidationResponseDto.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        authServiceClient.validateStorageLimit(requesterId, ownerId, bytesToAdd);

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(StorageValidationResponseDto.class)
        );

        HttpEntity<?> capturedEntity = entityCaptor.getValue();
        assertEquals(requesterId.toString(), capturedEntity.getHeaders().getFirst("X-User-ID"));
    }
}
