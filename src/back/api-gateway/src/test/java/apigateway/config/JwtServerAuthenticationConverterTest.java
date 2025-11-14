package apigateway.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServerAuthenticationConverterTest {

    @InjectMocks
    private JwtServerAuthenticationConverter converter;

    @Test
    void convert_WithBearerToken_ReturnsAuthentication() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt.token.here")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Authentication result = converter.convert(exchange).block();

        assertNotNull(result);
        assertEquals("jwt.token.here", result.getCredentials());
        assertEquals("jwt.token.here", result.getPrincipal());
    }

    @Test
    void convert_WithoutAuthorizationHeader_ReturnsEmpty() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Authentication result = converter.convert(exchange).block();

        assertNull(result);
    }

    @Test
    void convert_WithInvalidAuthHeader_ReturnsEmpty() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, "Basic username:password")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Authentication result = converter.convert(exchange).block();

        assertNull(result);
    }
}