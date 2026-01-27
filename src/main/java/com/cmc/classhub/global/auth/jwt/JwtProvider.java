package com.cmc.classhub.global.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private final long accessExpMillis;

    public JwtProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-exp-seconds}") long accessExpSeconds
    ) {
        // secret은 충분히 길게(권장 32바이트 이상)
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMillis = accessExpSeconds * 1000L;
    }

    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessExpMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long parseUserId(String token) {
        String sub = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.valueOf(sub);
    }
}
