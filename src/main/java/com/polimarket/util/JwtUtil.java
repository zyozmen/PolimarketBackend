package com.polimarket.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidad para manejo de tokens JWT
 * Proporciona métodos para generación, validación y extracción de información de tokens
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:polimarket-secret-key-super-secure-for-jwt-tokens-2025}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 horas en milisegundos
    private Long expiration;

    /**
     * Genera la clave secreta para firmar los tokens
     *
     * @return SecretKey generada desde el secreto configurado
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token JWT para un usuario
     *
     * @param usuario nombre de usuario
     * @param claims información adicional a incluir en el token
     * @return token JWT generado
     */
    public String generateToken(String usuario, Map<String, Object> claims) {
        return createToken(claims, usuario);
    }

    /**
     * Crea el token JWT con los claims y el subject especificados
     *
     * @param claims información adicional
     * @param subject usuario (subject del token)
     * @return token JWT
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae el nombre de usuario del token
     *
     * @param token token JWT
     * @return nombre de usuario
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token
     *
     * @param token token JWT
     * @return fecha de expiración
     */
    public LocalDateTime extractExpiration(String token) {
        Date expDate = extractClaim(token, Claims::getExpiration);
        return expDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Extrae un claim específico del token
     *
     * @param token token JWT
     * @param claimsResolver función para extraer el claim
     * @param <T> tipo del claim
     * @return valor del claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token
     *
     * @param token token JWT
     * @return todos los claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica si el token ha expirado
     *
     * @param token token JWT
     * @return true si el token ha expirado
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(LocalDateTime.now());
    }

    /**
     * Valida el token JWT
     *
     * @param token token JWT
     * @param usuario nombre de usuario a validar
     * @return true si el token es válido
     */
    public Boolean validateToken(String token, String usuario) {
        final String username = extractUsername(token);
        return (username.equals(usuario) && !isTokenExpired(token));
    }

    /**
     * Genera un token con información del empleado
     *
     * @param usuario nombre de usuario
     * @param empleadoId ID del empleado
     * @param roles roles del empleado
     * @return token JWT generado
     */
    public String generateTokenWithClaims(String usuario, Long empleadoId, java.util.Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("empleadoId", empleadoId);
        claims.put("roles", roles);
        return generateToken(usuario, claims);
    }
}
