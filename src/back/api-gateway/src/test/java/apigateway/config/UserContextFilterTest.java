package apigateway.config;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserContextFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private GatewayFilterChain filterChain;

    @InjectMocks
    private UserContextFilter userContextFilter;

    @Test
    void filter_WithValidToken_AddsUserHeaders() {
        // Given
        String token = "valid.jwt.token";
        Claims claims = mock(Claims.class);

        when(claims.getSubject()).thenReturn("user123");
        when(claims.get("roles", List.class)).thenReturn(List.of("USER", "ADMIN"));
        when(claims.get("name", String.class)).thenReturn("Juan Pérez");

        when(jwtService.validateToken(token)).thenReturn(claims);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Authentication auth = new UsernamePasswordAuthenticationToken("user123", null, List.of());
        SecurityContext securityContext = new SecurityContextImpl(auth);

        when(filterChain.filter(any())).thenReturn(Mono.empty());

        // When
        StepVerifier.create(
                        userContextFilter.filter(exchange, filterChain)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                )
                .verifyComplete();

        // Capture modified exchange
        var captor = org.mockito.ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(filterChain, times(1)).filter(captor.capture());

        ServerWebExchange modifiedExchange = captor.getValue();
        HttpHeaders modifiedHeaders = modifiedExchange.getRequest().getHeaders();

        // Then: validate headers added by the filter
        org.junit.jupiter.api.Assertions.assertEquals("user123",
                modifiedHeaders.getFirst("X-User-ID"));

        org.junit.jupiter.api.Assertions.assertEquals("USER,ADMIN",
                modifiedHeaders.getFirst("X-User-Roles"));

        org.junit.jupiter.api.Assertions.assertEquals("Juan Pérez",
                modifiedHeaders.getFirst("X-User-Name"));
    }

}