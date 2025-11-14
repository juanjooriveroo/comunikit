package apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

    /**
     * Convierte una petición HTTP en un objeto Authentication extrayendo el token JWT del header Authorization
     * 
     * @param exchange el intercambio de la petición web
     * @return Mono<Authentication> objeto Authentication con el token JWT, o Mono.empty() si no hay token
     */
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return Mono.just(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(token, token));
        }
        return Mono.empty();
    }
}
