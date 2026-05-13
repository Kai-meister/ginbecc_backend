package gov.kh.mcr.inspectorate.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long      accessExpiration;
    private final long      refreshExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}")
            long accessExpiration,
            @Value("${jwt.refresh-expiration}")
            long refreshExpiration) {

        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration  = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }


    public String generateAccessToken(
            String email,
            Integer userId,
            String role,
            List<String> permissions) {

        return Jwts.builder()
                .subject(email)
                .claim("userId",      userId)
                .claim("role",        role)
                .claim("permissions", permissions)
                .claim("type",        "ACCESS")
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis()
                                + accessExpiration))
                .signWith(signingKey)
                .compact();
    }


    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim("type", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis()
                                + refreshExpiration))
                .signWith(signingKey)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Integer getUserIdFromToken(String token) {
        Object id = parseClaims(token).get("userId");
        return id != null
                ? Integer.valueOf(id.toString())
                : null;
    }

    public String getRoleFromToken(String token) {
        return (String) parseClaims(token)
                .get("role");
    }


    public boolean validateToken(String token) {
        try {
            if (token == null
                    || token.isBlank()) {
                return false;
            }
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.debug("Token expired");
            return false;
        } catch (JwtException ex) {
            log.warn("Invalid JWT: {}",
                    ex.getMessage());
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return "REFRESH".equals(
                    parseClaims(token).get("type"));
        } catch (Exception ex) {
            return false;
        }
    }

    public long getRemainingExpiry(String token) {
        try {
            Date exp = parseClaims(token).getExpiration();
            return Math.max(0L,
                    exp.getTime()
                            - System.currentTimeMillis());
        } catch (Exception ex) {
            return 0L;
        }
    }


    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}