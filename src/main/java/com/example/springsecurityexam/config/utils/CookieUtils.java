package com.example.springsecurityexam.config.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    /**
     * cookie를 생성해주는 메소드
     */
    public void setCookie(HttpServletResponse response, String key, String value, Integer maxAge){

        if (key == null){
            key = "defaultKey";
        }
        Cookie cookie =  addCookie(key, value, maxAge);

        response.addCookie(cookie);
    }

    private Cookie addCookie(String key, String value, Integer maxAge){
        Cookie cookie = new Cookie(key, value);

        if(maxAge != null){
            cookie.setMaxAge(Math.toIntExact(maxAge));
        }

        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        cookie.setPath("/");

        return cookie;
    }
}
