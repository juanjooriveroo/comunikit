package apigateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Controlador que agrega la documentación OpenAPI de todos los microservicios
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SwaggerAggregatorController {

    private final WebClient.Builder webClientBuilder;

    /**
     * Endpoint que obtiene la documentación OpenAPI del microservicio auth
     * 
     * @return Mono<ResponseEntity<String>> respuesta con la documentación OpenAPI del servicio de autenticación
     */
    @GetMapping("/api-docs/auth")
    public Mono<ResponseEntity<String>> getAuthServiceDocs() {
        return fetchServiceDocs("http://auth-service:8081", "/api-docs");
    }

    /**
     * Endpoint que obtiene la documentación OpenAPI del microservicio board
     * 
     * @return Mono<ResponseEntity<String>> respuesta con la documentación OpenAPI del servicio de tablones
     */
    @GetMapping("/api-docs/board")
    public Mono<ResponseEntity<String>> getBoardServiceDocs() {
        return fetchServiceDocs("http://board-service:8082", "/api-docs");
    }

    /**
     * Endpoint que obtiene la documentación OpenAPI del microservicio admin
     * 
     * @return Mono<ResponseEntity<String>> respuesta con la documentación OpenAPI del servicio de administración
     */
    @GetMapping("/api-docs/admin")
    public Mono<ResponseEntity<String>> getAdminServiceDocs() {
        return fetchServiceDocs("http://admin-service:8083", "/api-docs");
    }

    /**
     * Método genérico para obtener documentación de cualquier servicio
     * 
     * @param serviceUrl URL base del servicio
     * @param docsPath ruta de la documentación OpenAPI
     * @return Mono<ResponseEntity<String>> respuesta con la documentación procesada o error si el servicio no está disponible
     */
    private Mono<ResponseEntity<String>> fetchServiceDocs(String serviceUrl, String docsPath) {
        log.debug("Obteniendo documentación de: {}{}", serviceUrl, docsPath);

        return webClientBuilder.build()
                .get()
                .uri(serviceUrl + docsPath)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .map(body -> {
                    String modifiedBody = body
                            .replace(serviceUrl, "")
                            .replace("http://localhost:8081", "https://back.comunikit.duckdns.org")
                            .replace("http://localhost:8082", "https://back.comunikit.duckdns.org")
                            .replace("http://localhost:8083", "https://back.comunikit.duckdns.org");
                    return ResponseEntity.ok(modifiedBody);
                })
                .onErrorResume(error -> {
                    log.error("Error obteniendo docs de {}: {}", serviceUrl, error.getMessage());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.SERVICE_UNAVAILABLE)
                            .body("{\"error\":\"Servicio no disponible: " + error.getMessage() + "\"}"));
                });
    }
}