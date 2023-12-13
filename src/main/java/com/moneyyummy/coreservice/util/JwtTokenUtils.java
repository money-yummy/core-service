package com.moneyyummy.coreservice.util;

import com.moneyyummy.coreservice.error.ErrorMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenUtils {

    private final static String AUTHORIZATION = "Authorization";

    @Value("${token.secretKey}")
    private String secretKey;

    @Value("${token.expireTimeMs}")
    private Long expireTimeMs;

    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies()).orElseGet(() -> new Cookie[0]);

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(AUTHORIZATION))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public String resolveToken(ServerHttpRequest request) {
        return Optional.ofNullable(request.getCookies().getFirst(AUTHORIZATION))
                .map(HttpCookie::getValue)
                .orElse(null);
    }

    public Claims validateAndExtractTokenClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (Exception e) {
            handleJwtException(e);
            return null;
        }
    }

    private void handleJwtException(Exception e) {
        ErrorMessage errorMessage = ErrorMessage.ofException(e);
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        throw new JwtException(errorMessage.getMessage());
    }
}
