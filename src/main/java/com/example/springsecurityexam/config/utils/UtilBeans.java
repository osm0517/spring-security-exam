package com.example.springsecurityexam.config.utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Component
public class UtilBeans {

    // 암호화에 사용하는 class를 bean으로 등록해서 사용
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    thymeleaf layout 을 사용하기 위해서 설정하는 bean
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

//    jSessionId 라는 cookie 가 생기는 것을 방지하는 bean
    @Bean
    public ServletContextInitializer servletContextInitializer(){
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {

                servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
                SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
                sessionCookieConfig.setHttpOnly(true);

            }
        };
    }
}
