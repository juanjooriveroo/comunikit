package apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsWebFilter implements WebFilter {

    @Value("${app.fqdn}")
    private String front;

    /**
     * Filtra las peticiones HTTP para añadir cabeceras CORS y manejar peticiones preflight
     *
     * @param exchange intercambio del servidor que contiene la petición y respuesta HTTP
     * @param chain cadena de filtros web a ejecutar
     * @return Mono<Void> vacío si es OPTIONS, o continúa la cadena de filtros
     * @throws RuntimeException si hay errores durante el procesamiento del filtro
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        String origin = request.getHeaders().getOrigin();

        String allowedOrigin = front.startsWith("http") ? front : "https://".concat(front);

        if (allowedOrigin.equals(origin)) {
            headers.add("Access-Control-Allow-Origin", origin);
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "*");
            headers.add("Access-Control-Allow-Credentials", "true");
            headers.add("Access-Control-Max-Age", "3600");
        }

        if (request.getMethod() == HttpMethod.OPTIONS) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }

        return chain.filter(exchange);
    }
}