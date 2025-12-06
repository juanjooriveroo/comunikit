package boardservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
            String url = String.format("%s/auth/validate-relation?tutorId=%s&dependentId=%s", 
                authServiceUrl, tutorId, dependentId);
            
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return result != null && result;
        } catch (Exception e) {
            return false;
        }
    }
}
