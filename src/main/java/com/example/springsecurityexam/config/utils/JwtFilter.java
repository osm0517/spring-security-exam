package com.example.springsecurityexam.config.utils;

import com.example.springsecurityexam.config.JWTConfig;
import com.nimbusds.oauth2.sdk.dpop.verifiers.AccessTokenValidationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.lang.constant.Constable;

@Slf4j
public class JwtFilter extends GenericFilterBean {

    private JWTConfig jwtConfig;

    private CookieUtils cookieUtils;

    private String accessTokenName = "X-AUTH-TOKEN";

//    @Value("${jwt.access_expire_time}")
    private long accessExpireTime = 1800000;

    public JwtFilter(JWTConfig jwtConfig, CookieUtils cookieUtils){
        this.cookieUtils = cookieUtils;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

//        refresh token 받아오기
        String refreshToken = jwtConfig.resolveRefreshToken((HttpServletRequest) request);
//        access token 받아오기
        String accessToken = jwtConfig.resolveAccessToken((HttpServletRequest) request);

//        access token 만료 안됨
        if(accessToken != null && jwtConfig.validateAccessToken(accessToken)){
            UsernamePasswordAuthenticationToken authentication = jwtConfig.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }else{
//            log.debug("refresh token = {}", refreshToken);
//            log.debug("valideate result = {}", jwtConfig.validateRefreshToken(refreshToken));
//            access token 만료되고 refresh token 만료안됨
            if(refreshToken != null && jwtConfig.validateRefreshToken(refreshToken)){
//            if(true){
//                log.debug("access token expire");
//                log.debug("access token name = {}", accessTokenName);
                String newToken = jwtConfig.createAccessToken(refreshToken);
                ((HttpServletResponse) response).addCookie(cookieUtils.setCookie(accessTokenName, newToken, accessExpireTime));
            } else{
//                refresh token 만료됨
                log.debug("refresh token expire");
            }
        }
//        refresh token

        chain.doFilter(request, response);
    }
}
