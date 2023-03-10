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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JWTConfig {
    @Value("${jwt.access_secret_key}")
    private String accessSecretKey;

    @Value("${jwt.refresh_secret_key}")
    private String refreshSecretKey;

    @Value("${jwt.access_expire_time}")
    private long accessExpireTime;

    @Value("${jwt.refresh_expire_time}")
    private long refreshExpireTime;

    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;

    @Value("${jwt.issuer}")
    private String issuer;

    private final UserDetailsServiceImpl userDetailsService;

    private final MemberRepository memberRepository;

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
//                ???????????? ???????????? ???????????? ???????????? ??? ????????? ??? ????????? ???
                .setIssuer(issuer)
                .setIssuedAt(now)
//                ???????????? 30???
                .setExpiration(new Date(now.getTime() + accessExpireTime))
//                body??? ???????????? ????????? ??????
                .claim("userId", member.getUserId())
                .claim("email", member.getEmail())
//                ???????????? ??? ??????????????? key??? ??????
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

    // JWT ???????????? ?????? ?????? ??????
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
     * Request??? Header?????? token ?????? ???????????????. "X-AUTH-TOKEN" : "TOKEN???'
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
     * refresh ????????? ????????? + ???????????? ??????
     * token ????????? ?????? ???????????? ???????????? false
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
     * ??????????????? ????????? ??? ??? ?????? ????????? ?????? ???????????? ??????????????? ?????????
     * ???????????? true
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

    public Boolean validateAccessToken(String token) {
        Date now = new Date();
        try {

            Claims body = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody();

            return body.getIssuer().equals(issuer) && body.getExpiration().after(now);

        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdByRefreshToken(String refreshToken){

        return (String) Jwts.parser()
                .setSigningKey(refreshSecretKey)
                .parseClaimsJws(refreshToken)
                .getBody()
                .get("userId");

    }
}
