package authservice.utils;

import authservice.entity.User;
import authservice.entity.Rol;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private User testUser;
    private final String jwtSecret = "testSecretKeyWithMinimum32CharactersLength";
    private final long expirationMs = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "accessExpirationMs", expirationMs);

        Rol testRole = Rol.builder()
                .name("USER")
                .build();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .rol(testRole)
                .build();
    }

    @Test
    void generateToken_ShouldReturnValidJwtToken() {
        String token = jwtUtils.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(testUser.getId().toString(), claims.getSubject());
        assertEquals(testUser.getName(), claims.get("name"));
        assertEquals(testUser.getEmail(), claims.get("email"));
        assertEquals(testUser.getRol().getName(), claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());

        long expectedExpiration = claims.getIssuedAt().getTime() + expirationMs;
        assertEquals(expectedExpiration, claims.getExpiration().getTime());
    }

    @Test
    void generateToken_ShouldHaveCorrectExpiration() {
        String token = jwtUtils.generateToken(testUser);

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        long actualExpirationMs = expiration.getTime() - issuedAt.getTime();
        assertEquals(expirationMs, actualExpirationMs, 1000); // Allow 1 second tolerance
    }

    @Test
    void getSigningKey_ShouldReturnValidKey() {
        Object key = ReflectionTestUtils.invokeMethod(jwtUtils, "getSigningKey");

        assertNotNull(key);
    }
}