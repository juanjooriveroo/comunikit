package apigateway.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    /**
     * Autentica un token JWT y crea un objeto Authentication con los roles del usuario
     * 
     * @param authentication objeto de autenticación que contiene el token JWT
     * @return Mono<Authentication> objeto de autenticación con usuario y roles, o error si el token es inválido
     * @throws RuntimeException si el token no contiene roles o es inválido
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        try {
            Claims claims = jwtService.validateToken(token);
            String username = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            if (roles == null || roles.isEmpty()) {
                log.warn("Token sin roles para usuario: {}", username);
                return Mono.error(new RuntimeException("Token sin roles"));
            }

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> {
                        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                        return new SimpleGrantedAuthority(authority);
                    })
                    .collect(Collectors.toList());

            var auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );

            log.debug("Usuario autenticado: {} con roles: {}", username, authorities);
            return Mono.just(auth);

        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return Mono.error(e);
        }
    }
}