package com.example.springsecurityexam.auth.handler;

import com.example.springsecurityexam.auth.userDetails.CustomUserDetails;
import com.example.springsecurityexam.auth.userDetails.OAuth2UserDetailsImpl;
import com.example.springsecurityexam.config.utils.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.debug("login success");

        Object principal = authentication.getPrincipal();

        HttpSession session = request.getSession();

//        form login
        if(principal.getClass().equals(CustomUserDetails.class)){
            long id = ((CustomUserDetails) principal).getId();
            session.setAttribute(SessionUtils.session_login_id, id);
        }else{
//            oauth2 login
            long id = ((OAuth2UserDetailsImpl) principal).getId();
            session.setAttribute(SessionUtils.session_login_id, id);
        }
        response.sendRedirect("/");
    }
}
