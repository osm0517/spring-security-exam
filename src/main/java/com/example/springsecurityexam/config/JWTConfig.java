package com.example.springsecurityexam.config;

import com.example.springsecurityexam.auth.CustomUserDetails;
import com.example.springsecurityexam.auth.service.CustomUserDetailsService;
import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.repository.JPAMemberRepository;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.lang.constant.Constable;
import java.util.*;

@Configuration
@Slf4j
public class JWTConfig {
    @Value("${jwt.access_secret_key}")
    private String accessSecretKey;

    @Value("${jwt.refresh_secret_key}")
    private String refreshSecretKey;

//    @Value("${jwt.access_expire_time}")
    private static long accessExpireTime = 1800000;

//    @Value("${jwt.refresh_expire_time}")
    private static long refreshExpireTime = 1209600000;

    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;

    private final CustomUserDetailsService userDetailsService;

    private final JPAMemberRepository memberRepository;

    public JWTConfig(CustomUserDetailsService userDetailsService, JPAMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes());
    }

    public String createAccessToken(String refreshToken) {

        String userId = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).getBody().getSubject();

        Member member = memberRepository.findByUserId(userId);

        Claims payload = Jwts.claims().setSubject(userId); // JWT payload 에 저장되는 정보단위
        payload.put("name", member.getName());
        payload.put("role", member.getAuth().toString());

        Date now = new Date();
        log.debug("create userId = {}", userId);
        return Jwts.builder()
                .setClaims(payload) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessExpireTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    public String createRefreshToken(String userId) {
        log.debug("expireTime = {}", refreshExpireTime);
        log.debug("key = {}", refreshSecretKey);
        Claims claims = Jwts.claims().setSubject(userId); // JWT payload 에 저장되는 정보단위
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + refreshExpireTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String userId = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody().getSubject();
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
     * @param request
     * @return token 값
     */
    public String resolveAccessToken(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            String accessToken = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(accessTokenName)) {
                    accessToken = cookie.getValue();
                }
            }
            return accessToken;
        }catch (NullPointerException e){
            return null;
        }
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            String refreshToken = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(refreshTokenName)) {
                    refreshToken = cookie.getValue();
                }
            }
            return refreshToken;
        }catch (NullPointerException e){
            return null;
        }
    }

    /**
     * refresh 토큰의 유효성 + 만료일자 확인
     * token 시간이 현재 시간보다 이전이면 false
     * @param token
     * @return boolean
     */
    public boolean validateRefreshToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateAccessToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token);
//            log.debug("fuck claims = {}", claims);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
//            log.debug("fuck signature");
            return false;
        }
    }
}
