package kr.pianobear.application.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;
    private final RedisRepository redisRepository;

    public JwtUtil(@Value("${jwt.secret}") String secretKey,
                   @Value("${jwt.access-expiration-time}") long accessTokenExpTime,
                   @Value("${jwt.refresh-expiration-time}") long refreshTokenExpTime,
                   RedisRepository redisRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
        this.redisRepository = redisRepository;
    }

    public String createAccessToken(Member member) {
        return createToken(member, accessTokenExpTime);
    }

    public String createRefreshToken(Member member) {
        return createToken(member, refreshTokenExpTime);
    }

    private String createToken(Member member, long expireTime) {
        String jti = UUID.randomUUID().toString();

        Claims claims = Jwts.claims()
                .add("jti", jti)
                .add("username", member.getId())
                .add("role", member.getRole())
                .build();

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(tokenValidity.toInstant()))
                .signWith(this.secretKey)
                .compact();
    }

    public String parseUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String jti = claims.get("jti", String.class);

            if (redisRepository.hasKey(jti))
                return false;

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String parseJti(String token) {
        Claims claims = parseClaims(token);
        return claims.get("jti", String.class);
    }

    public int parseIat(String token) {
        Claims claims = parseClaims(token);
        return claims.get("iat", Integer.class);
    }

    public int parseExp(String token) {
        Claims claims = parseClaims(token);
        return claims.get("exp", Integer.class);
    }
}
