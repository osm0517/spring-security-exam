package com.example.springsecurityexam.config.utils;

import com.example.springsecurityexam.config.JWTConfig;
import com.example.springsecurityexam.domain.RefreshToken;
import com.example.springsecurityexam.repository.RefreshTokenRepository;
import com.nimbusds.oauth2.sdk.dpop.verifiers.AccessTokenValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.lang.constant.Constable;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final JWTConfig jwtConfig;

    private final CookieUtils cookieUtils;

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;
    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        log.debug("dofilter");

//        없으면 null
//        refresh token 받아오기
        String refreshToken = jwtConfig.resolveRefreshToken((HttpServletRequest) request);
//        access token 받아오기
        String accessToken = jwtConfig.resolveAccessToken((HttpServletRequest) request);

//        access token 만료 안됨
        if(accessToken != null && jwtConfig.validateAccessToken(accessToken)){
            UsernamePasswordAuthenticationToken authentication = jwtConfig.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }else{
//            access token 만료되고 refresh token 존재안함
            if(refreshToken == null) {
                SecurityContextHolder.clearContext();
                log.debug("refresh token expire");
            }
//            access token 만료되고 refresh token 존재함
            else{
                try{
                    RefreshToken findRefreshToken = refreshTokenRepository.findByValue(refreshToken)
                            .orElseThrow(NoSuchElementException::new);

                    String newRefreshToken = jwtConfig.createRefreshToken(findRefreshToken.getUserId());

                    findRefreshToken.changRefreshToken(newRefreshToken);

                    refreshTokenRepository.save(findRefreshToken);

//                만료 안됨
                    if(jwtConfig.validateRefreshToken(refreshToken)){
                        String newToken = jwtConfig.createAccessToken(refreshToken);

                        cookieUtils.setCookie((HttpServletResponse) response, accessTokenName, newToken, null);
                    }
//                만료 됨
                    else{
                        String newAccessToken = jwtConfig.createAccessToken(newRefreshToken);

                        cookieUtils.setCookie((HttpServletResponse) response, accessTokenName, newAccessToken, null);
                    }
                    cookieUtils.setCookie((HttpServletResponse) response, refreshTokenName, newRefreshToken, null);

                }catch (NoSuchElementException e){
//                    정상적이지 못한 refresh token으로 요청이 들어오면 공격으로 판단하고 모든 token을 삭제
                    refreshTokenRepository.deleteAllInBatch();
//                    쿠키도 삭제
                    cookieUtils.setCookie((HttpServletResponse) response, refreshTokenName, null, 0);
                    SecurityContextHolder.clearContext();
                }
            }
        }
        chain.doFilter(request, response);
    }
}
