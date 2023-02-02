package com.example.springsecurityexam.auth.handler.loginHandler;

import com.example.springsecurityexam.auth.userDetails.CustomUserDetails;
import com.example.springsecurityexam.auth.userDetails.OAuth2UserDetailsImpl;
import com.example.springsecurityexam.config.JWTConfig;
import com.example.springsecurityexam.config.utils.CookieUtils;
import com.example.springsecurityexam.config.utils.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class SuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final JWTConfig jwtConfig;
    private final CookieUtils cookieUtils;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.debug("login success");

        Object principal = authentication.getPrincipal();

        String username;

        if(principal.getClass().equals(CustomUserDetails.class)){
//              form login
            username = ((CustomUserDetails) principal).getUsername();
        }else{
//              oauth2 login
            username = ((OAuth2UserDetailsImpl) principal).getUsername();
        }
        String refreshToken = jwtConfig.createRefreshToken(username);

        cookieUtils.setCookie(response, refreshTokenName, refreshToken, null);

        response.sendRedirect("/");
    }
}
