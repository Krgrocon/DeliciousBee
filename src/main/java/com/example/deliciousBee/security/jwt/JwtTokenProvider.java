package com.example.deliciousBee.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.example.deliciousBee.model.member.BeeMember;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long jwtExpirationInMs;
    private final long emailVerificationExpirationInMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long jwtExpirationInMs,
            @Value("${jwt.emailVerificationExpiration}") long emailVerificationExpirationInMs) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
        this.emailVerificationExpirationInMs = emailVerificationExpirationInMs;
    }

    // JWT 토큰 생성
    public String generateToken(Authentication authentication) {
        BeeMember userPrincipal = (BeeMember) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getMember_id()) // 사용자 ID 설정
                .claim("role", userPrincipal.getRole().name()) // 사용자 권한 클레임 추가
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘으로 서명
                .compact();
    }

    // 사용자 정보로 JWT 토큰 생성 (OAuth2 용도)
    public String generateToken(BeeMember beeMember) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(beeMember.getMember_id())
                .claim("role", beeMember.getRole().name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256) // HS256으로 서명
                .compact();
    }

    // JWT에서 사용자 ID 추출
    public String getMemberIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // 서브젝트로 사용자 ID 반환
    }

    // JWT 토큰 유효성 검사
    public boolean validateToken(String authToken) {
        try {
            System.out.println("Validating token: " + authToken); // 토큰 값 출력
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            System.out.println("Token is valid: " + claims.getBody()); // 파싱한 클레임 출력
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token is expired: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT Token: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT claims string is empty or invalid: " + e.getMessage());
        }
        return false;
    }


    // 이메일 인증용 JWT 토큰 생성
    public String generateEmailVerificationToken(String email, int verificationCode) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + emailVerificationExpirationInMs); // 5분 후 만료

        return Jwts.builder()
                .setSubject(email)
                .claim("verificationCode", verificationCode)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 이메일 인증용 토큰에서 인증 코드 추출
    public Integer getVerificationCodeFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("verificationCode", Integer.class);
    }

    // 이메일 인증용 토큰에서 이메일 추출
    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
