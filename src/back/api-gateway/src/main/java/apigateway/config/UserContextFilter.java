package apigateway.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserContextFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .filter(auth -> auth.isAuthenticated())
                .map(auth -> addUserHeaders(exchange))
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    /**
     * Solo modifica el exchange añadiendo headers.
     * NO llama al chain.
     */
    private ServerWebExchange addUserHeaders(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                Claims claims = jwtService.validateToken(token);

                String userId = claims.getSubject();
                List<String> roles = claims.get("roles", List.class);
                String userName = claims.get("name", String.class);

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-ID", userId)
                        .header("X-User-Roles", roles != null ? String.join(",", roles) : "")
                        .header("X-User-Name", userName != null ? userName : "")
                        .build();

                log.debug("Headers de usuario añadidos: ID={}, Roles={}", userId, roles);

                return exchange.mutate().request(modifiedRequest).build();

            } catch (Exception e) {
                log.error("Error extrayendo información del token: {}", e.getMessage());
            }
        }

        return exchange;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}