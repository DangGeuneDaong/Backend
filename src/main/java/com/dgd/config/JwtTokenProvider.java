package com.dgd.config;

import com.dgd.exception.error.AuthenticationException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.Base64;
import java.util.Date;

import static com.dgd.exception.message.AuthErrorMessage.INVALID_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret.key}")
    private String secretKey;

    private static final String AUTHORITIES_KEY = "Authorization";
    private static final String BEARER_TYPE = "Bearer";
    private final Long accessTokenValidTime = 1000L * 60 * 60 * 6;
    private final Long refreshTokenValidTime = 2 * 24 * 60 * 60 * 1000L;

    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    public String generateAccessToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());

        Date now = new Date();
        Date expiresIn = new Date(now.getTime() + accessTokenValidTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiresIn)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());

        Date now = new Date();
        Date expiresIn = new Date(now.getTime() + refreshTokenValidTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiresIn)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthenticationByAccessToken(String access_token) {
        String userPrincipal = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(access_token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Authentication getAuthenticationByRefreshToken(String refresh_token) {
        String userPrincipal = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refresh_token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTHORITIES_KEY);
        if (bearerToken != null && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getPayload(String token) {
        String[] tokenParts = token.split("\\.");
        if (tokenParts.length == 3) {
            String encodedPayload = tokenParts[1];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedPayload);
            return new String(decodedBytes);
        } else {
            throw new AuthenticationException(INVALID_TOKEN);
        }
    }

    public String getPayloadSub(String token) { // payload 에 sub : userId 값 추출
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long getPayloadExp(String token) { // payload 에 exp : 유효 시간 추출
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration().getTime();
    }



    public Boolean validateAccessToken(String token) {
        try {
            if(redisTemplate.opsForValue().get(token).equals("blacklist")) {
                throw new AuthenticationException(INVALID_TOKEN);
            }
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }


    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
