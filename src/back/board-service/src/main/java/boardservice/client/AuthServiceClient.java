package boardservice.client;

import boardservice.dto.StorageValidationRequestDto;
import boardservice.dto.StorageValidationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Cliente para comunicarse con el auth-service
 */
@Component
@RequiredArgsConstructor
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    @Value("${auth-service.url}")
    private String authServiceUrl;

    /**
     * Valida si existe una relación tutor-dependiente entre dos usuarios
     * @return true si la relación es válida
     */
    public boolean validateUserRelation(UUID tutorId, UUID dependentId) {
        try {
            String url = String.format("%s/validate-relation?tutorId=%s&dependentId=%s",
                    authServiceUrl, tutorId, dependentId);

            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return result != null && result;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica en auth-service si el almacenamiento disponible permite subir bytesToAdd
     */
    public StorageValidationResponseDto validateStorageLimit(UUID requesterId, UUID ownerId, long bytesToAdd) {
        String url = authServiceUrl.concat("/storage/validate");

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-ID", requesterId.toString());

        StorageValidationRequestDto request = new StorageValidationRequestDto(ownerId, bytesToAdd);
        HttpEntity<StorageValidationRequestDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<StorageValidationResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                StorageValidationResponseDto.class
        );

        if (response.getBody() == null || !response.getBody().allowed()) {
            throw new RuntimeException("La imagen supera el límite de almacenamiento");
        }

        return response.getBody();
    }
}
