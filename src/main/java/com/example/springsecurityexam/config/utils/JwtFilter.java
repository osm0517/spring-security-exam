package com.example.springsecurityexam.config.utils;

import com.example.springsecurityexam.config.JWTConfig;
import com.example.springsecurityexam.domain.RefreshToken;
import com.example.springsecurityexam.repository.RefreshTokenRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTConfig jwtConfig;

    private final CookieUtils cookieUtils;

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;
    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        없으면 null
//        refresh token 받아오기
        String refreshToken = jwtConfig.resolveRefreshToken(request);
//        access token 받아오기
        String accessToken = jwtConfig.resolveAccessToken(request);

//        access token 유효함
        if(accessToken != null && jwtConfig.validateAccessToken(accessToken)){
//            security context에 저장이 안돼있으면 context 저장
            setAuthentication(accessToken);
        }else{
//             access token 유효하지 않고 refresh token 유효함
            if(refreshToken != null && jwtConfig.validateRefreshToken(refreshToken)) {

                String userId = jwtConfig.getUserIdByRefreshToken(refreshToken);
                RefreshToken findRefreshToken = refreshTokenRepository.findByUserId(userId)
                        .orElse(null);

                if(findRefreshToken == null){
                    log.debug("최초 로그인");

                    String newAccessToken = jwtConfig.createAccessToken(refreshToken);

                    refreshTokenRepository.save(new RefreshToken(userId, refreshToken));

                    cookieUtils.setCookie( response, accessTokenName, newAccessToken, null);
                }else{
//                    db에 있는 token도 유효하고 cookie 값과도 동일함
                    if (validateRefreshToken(refreshToken, findRefreshToken)) {
//                        새로운 token을 만듦
                        String newAccessToken = jwtConfig.createAccessToken(refreshToken);
                        String newRefreshToken = jwtConfig.createRefreshToken(userId);
//                        한 번 사용한 token은 교체함
                        findRefreshToken.changRefreshToken(newRefreshToken);

                        refreshTokenRepository.save(findRefreshToken);

                        cookieUtils.setCookie( response, accessTokenName, newAccessToken, null);
                        cookieUtils.setCookie( response, refreshTokenName, newRefreshToken, null);

                        setAuthentication(newAccessToken);

                    }else{
                        log.error("not match database refresh token and refresh token cookie");
                        log.error("database refresh token = {}", findRefreshToken);
                        log.error("refresh token cookie = {}", refreshToken);
//                        비정상적인 요청으로 판단하고 삭제함
                        refreshTokenRepository.delete(findRefreshToken);
                    }
                }
            }else{
//            access token 유효하지 않고 refresh token 유효하지 않음
                init(response);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication = jwtConfig.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private void init(ServletResponse res) {
        HttpServletResponse response = (HttpServletResponse) res;
//                    쿠키도 삭제
        cookieUtils.setCookie(response, refreshTokenName, null, 0);
        cookieUtils.setCookie(response, accessTokenName, null, 0);
//                    초기화도 진행
        SecurityContextHolder.clearContext();
    }

//      cookie에 token이 유효한지를 판단
    private boolean validateRefreshToken(String refreshToken, RefreshToken findRefreshToken) {

        return findRefreshToken.getValue().equals(refreshToken);
    }
}
