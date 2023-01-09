package com.example.springsecurityexam.config.utils;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    /**
     * cookie를 생성해주는 메소드
     * @param key
     * @param value
     * @param maxAge
     * @return
     */
    public Cookie setCookie(String key, String value, long maxAge){

        if (key == null){
            key = "defaultKey";
        }

        Cookie cookie = new Cookie(key, value);

        cookie.setMaxAge(Math.toIntExact(maxAge));
        cookie.setHttpOnly(true);

        return cookie;
    }
}
