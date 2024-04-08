package com.idrsys.toyprojectbackend.config.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.idrsys.toyprojectbackend.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secretKey){
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateRefreshToken(Member member) {
        long now = new Date().getTime();

        return Jwts.builder()
                .claim("id", UUID.randomUUID().toString())
                .claim("name", member.getUsername())
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String validationRefreshToken(String refreshToken){
        try {
            Jwt<JwsHeader, Claims> parseRefreshToken = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken);

            String name = parseRefreshToken.getPayload().get("name", String.class);

            return name;
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid refresh token");
        }

    }

    public String generateAccessToken(Map<String, Object> claims, Authentication authentication, int seconds) {
        long now = new Date().getTime();
        Date expiresAt = new Date(now + 1000L * seconds);
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .claim("body", jsonToStr(claims))
                .claim("auth", authorities)
                .expiration(expiresAt)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public static Object jsonToStr(Map<String, Object> claims) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(claims);
        } catch (JsonProcessingException e) {
            log.error("에러가 발생하였습니다 : {}", e.getMessage());
            throw new DataIntegrityViolationException("임시 예외 선언");
        }
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        Map<String, Object> claimsToMap = getClaims(accessToken);
        UserDetails principal = new User((String) claimsToMap.get("id"), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Map<String, Object> getClaims(String token) {
        String body = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("body", String.class);

        return jsonToMap(body);
    }

    private Map<String, Object> jsonToMap(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(jsonStr, LinkedHashMap.class);
        } catch(JsonProcessingException e) {
            throw new DataIntegrityViolationException("..");
        }
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public boolean isTokenExpired(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expirationDate = claims.getExpiration();

        return expirationDate.before(new Date());
    }

}
