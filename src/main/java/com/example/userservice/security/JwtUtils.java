package com.example.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    // Nota: La clave debe ser lo suficientemente larga para HS256 (mínimo 32 caracteres)
    private static final String SECRET_KEY = "TuClaveSecretaSuperSeguraParaElProyectoDeGeronimo12345";
    private static final long EXPIRATION_TIME = 3600000; // 1 hora en milisegundos

    /**
     * Genera la llave de firma. 
     * En la versión 0.12.x, se prefiere trabajar directamente con SecretKey.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
 
    /**
     * Crea un nuevo token JWT para el usuario.
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey()) // El algoritmo se detecta automáticamente por el tamaño de la llave
                .compact();
    }

    /**
     * Valida si el token es estructuralmente correcto y no ha expirado.
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Aquí podrías loguear por qué falló (expirado, firma inválida, etc.)
            return false;
        }
    }

    /**
     * Extrae el email (subject) del token.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Método genérico para extraer cualquier información (Claim) del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload(); // .getPayload() reemplaza a .getBody() en 0.12.x
        return claimsResolver.apply(claims);
    }
}