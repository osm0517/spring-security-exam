package com.example.springsecurityexam.config;

import com.example.springsecurityexam.auth.userDetails.CustomUserDetails;
import com.example.springsecurityexam.auth.service.UserDetailsServiceImpl;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.repository.MemberRepository;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Duration;
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

    @Value("${jwt.issuer}")
    private String issuer;

    private final UserDetailsServiceImpl userDetailsService;

    private final MemberRepository memberRepository;

    public JWTConfig(UserDetailsServiceImpl userDetailsService, MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes());
    }

    public String createAccessToken(String refreshToken) {

        Date now = new Date();
        String userId = Jwts.parser()
                .setSigningKey(refreshSecretKey)
                .parseClaimsJws(refreshToken)
                .getBody()
                .get("userId").toString();

        Optional<Member> wrapMember = memberRepository.findByUserId(userId);

        if (wrapMember.isEmpty()) {
            throw new UsernameNotFoundException("username not found");
        }

        Member member = wrapMember.get();

        return Jwts.builder()
//                발급자를 지정해서 혹시라도 조작했을 때 확인할 수 있도록 함
                .setIssuer(issuer)
                .setIssuedAt(now)
//                만료시간 30분
                .setExpiration(new Date(now.getTime() + accessExpireTime))
//                body에 사용자의 정보를 담음
                .claim("userId", member.getUserId())
                .claim("email", member.getEmail())
//                암호화를 할 알고리즘과 key를 설정
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)
                .compact();
    }

    public String createRefreshToken(String userId) {

        Date now = new Date();

        return Jwts.builder()
                .setIssuer(issuer)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpireTime))
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {

        String userId = Jwts.parser()
                .setSigningKey(accessSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("userId").toString();

        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
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
     */
    public boolean validateRefreshToken(String token) {
        Date now = new Date();
        try {

            Claims body = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token).getBody();
            return body.getIssuer().equals(issuer) && body.getExpiration().after(now);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 파라미터로 넘어온 값 두 개를 더해서 현재 시간보다 이후인지를 판단함
     * 이후이면 true
     */
    public boolean validateRefreshTokenFromDB(Date createdDate, @NotNull long expire) {
        Date now = new Date();
        try {

            Date expireDate = new Date(createdDate.getTime() + expire);
            return expireDate.after(now);

        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateAccessToken(String token) {
        Date now = new Date();
        try {

            Claims body = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody();
            return body.getIssuer().equals(issuer) && body.getExpiration().after(now);

        } catch (Exception e) {
            return false;
        }
    }
}
