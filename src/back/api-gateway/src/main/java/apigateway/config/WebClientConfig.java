package apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * Configura el builder de WebClient para realizar peticiones HTTP reactivas
     * 
     * @return WebClient.Builder builder configurado para crear instancias de WebClient
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}