package apigateway.config;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationManagerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthenticationManager jwtAuthenticationManager;

    @Test
    void authenticate_WithValidToken_ReturnsAuthentication() {
        String token = "valid.jwt.token";
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user123");
        when(claims.get("roles", List.class)).thenReturn(List.of("USER", "ADMIN"));
        when(jwtService.validateToken(token)).thenReturn(claims);

        Authentication authToken = new UsernamePasswordAuthenticationToken("principal", token);

        Authentication result = jwtAuthenticationManager.authenticate(authToken).block();

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals("user123", result.getName());
        assertEquals(2, result.getAuthorities().size());
        verify(jwtService).validateToken(token);
    }

    @Test
    void authenticate_WithTokenWithoutRoles_ThrowsException() {
        String token = "token.sin.roles";
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user123");
        when(claims.get("roles", List.class)).thenReturn(null);
        when(jwtService.validateToken(token)).thenReturn(claims);

        Authentication authToken = new UsernamePasswordAuthenticationToken("principal", token);

        assertThrows(RuntimeException.class, () ->
                jwtAuthenticationManager.authenticate(authToken).block()
        );
    }
}