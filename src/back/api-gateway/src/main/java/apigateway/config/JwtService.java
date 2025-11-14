package apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Obtiene la clave de firma para validar tokens JWT
     * 
     * @return SecretKey clave de firma generada a partir del secret configurado
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Valida un token JWT y extrae sus claims
     * 
     * @param token token JWT a validar
     * @return Claims objeto con toda la información contenida en el token
     * @throws Exception si el token es inválido, está expirado o no se puede verificar
     */
    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}