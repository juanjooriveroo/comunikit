package apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // Secret de prueba (debe tener al menos 256 bits / 32 caracteres para HS256)
    private static final String TEST_SECRET = "test-secret-key-for-jwt-token-validation-minimum-256-bits";

    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Inyectar el secret manualmente en el campo privado
        ReflectionTestUtils.setField(jwtService, "jwtSecret", TEST_SECRET);

        // Crear la clave de firma para los tests
        signingKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void validateToken_WithValidToken_ReturnsClaims() {
        // Given: crear un token válido
        String token = Jwts.builder()
                .subject("user123")
                .claim("roles", List.of("ROLE_USER", "ROLE_ADMIN"))
                .claim("name", "Juan Pérez")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(signingKey)
                .compact();

        // When: validar el token
        Claims claims = jwtService.validateToken(token);

        // Then: verificar los claims
        assertNotNull(claims);
        assertEquals("user123", claims.getSubject());
        assertEquals("Juan Pérez", claims.get("name"));

        List<?> roles = claims.get("roles", List.class);
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    void validateToken_WithExpiredToken_ThrowsException() {
        // Given: crear un token que ya expiró
        String expiredToken = Jwts.builder()
                .subject("user123")
                .issuedAt(new Date(System.currentTimeMillis() - 7200000)) // hace 2 horas
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // expiró hace 1 hora
                .signWith(signingKey)
                .compact();

        // When & Then: debe lanzar excepción
        assertThrows(Exception.class, () -> jwtService.validateToken(expiredToken));
    }

    @Test
    void validateToken_WithInvalidToken_ThrowsException() {
        // Given: un token completamente inválido
        String invalidToken = "token.invalido.malformado";

        // When & Then: debe lanzar excepción
        assertThrows(Exception.class, () -> jwtService.validateToken(invalidToken));
    }

    @Test
    void validateToken_WithTamperedToken_ThrowsException() {
        // Given: crear un token válido y luego modificarlo
        String token = Jwts.builder()
                .subject("user123")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(signingKey)
                .compact();

        // Alterar el token (cambiar un carácter en el medio)
        String tamperedToken = token.substring(0, token.length() - 10) + "X" + token.substring(token.length() - 9);

        // When & Then: debe lanzar excepción porque la firma no coincide
        assertThrows(Exception.class, () -> jwtService.validateToken(tamperedToken));
    }

    @Test
    void validateToken_WithDifferentSigningKey_ThrowsException() {
        // Given: crear un token con una clave diferente
        SecretKey differentKey = Keys.hmacShaKeyFor(
                "another-secret-key-for-testing-different-signature-validation".getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("user123")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(differentKey)
                .compact();

        // When & Then: debe lanzar excepción porque la firma es diferente
        assertThrows(Exception.class, () -> jwtService.validateToken(token));
    }
}