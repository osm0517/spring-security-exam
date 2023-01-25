package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.config.utils.CookieUtils;
import com.example.springsecurityexam.config.utils.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

//@Controller @RequestMapping(value = "/code")
@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginController {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;

//    ----- base URL start -----

    private static final String kakaoURL = "https://kauth.kakao.com";

    private static final String googleURL = "https://apitest.acme.com";

    private static final String naverURL = "https://nid.naver.com";

//    ----- base URL end -----

    private final CookieUtils cookieUtils;

    @RequestMapping("/{target}")
    public String oauthLogin(
            String code,
            @PathVariable String target,
            HttpServletResponse response,
            HttpServletRequest request
    ){
        log.debug("login oauth2 redirect uri");

        String tarterURL =  getOAuthURL(target);
        String targetURI = getOAuthURI(target);


//        오류가 발생하면 처리해줄 페이지를 제작해야함
        if(tarterURL == null){
            log.error("wrong query string : name = {}", target);
            return "redirect:/login";
        }

        WebClient client = getWebClient(tarterURL);

        MultiValueMap<String, String> body = getBody(code, target);

        JSONObject results = client.post()
                .uri(targetURI)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

//        results 가 null 인 것처럼 오류가 발생하면 처리를 해줄 페이지 제작하면 대체하기
        if(results == null){
            return "redirect:/login";
        }

//        log.debug("results = {}", results);

        String accessTokenValue = results.get("access_token").toString();
        String refreshTokenValue = results.get("refresh_token").toString();
        long accessTokenExpireTime = Long.parseLong(results.get("expires_in").toString());
        long refreshTokenExpireTime = 5183999;

        if(results.containsKey("refresh_token_expires_in")) {
            refreshTokenExpireTime = Long.parseLong(results.get("refresh_token_expires_in").toString());
        }

        log.debug("debug = {}, {}, {}, {}", accessTokenValue, accessTokenExpireTime, refreshTokenValue, refreshTokenExpireTime);

//        response.addCookie(cookieUtils.setCookie(accessTokenName,
//                accessTokenValue,
//                accessTokenExpireTime));
//        response.addCookie(cookieUtils.setCookie(refreshTokenName,
//                refreshTokenValue,
//                refreshTokenExpireTime));
        HttpSession session = request.getSession();
//        session.setAttribute(SessionUtils.session_login_id,);

        return "redirect:/";

    }

    private String getOAuthURI(String target) {
        return switch (target){
            case "kakao" -> "/oauth/token";
            case "google" -> "/oauth/token";
            case "naver" -> "/oauth2.0/token";
            default -> null;
        };
    }

    private MultiValueMap<String, String> getBody(String code, String target) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.put("grant_type", Collections.singletonList("authorization_code"));
        body.put("redirect_uri", Collections.singletonList("http://localhost:8080/code/"+target));
        body.put("code", Collections.singletonList(code));

        switch (target){
            case "kakao" -> {
                body.put("client_id", Collections.singletonList(kakaoClientId));
            }
            case "google" -> {
                body.put("client_id", Collections.singletonList(googleClientId));
                body.put("client_secret", Collections.singletonList(googleClientSecret));
            }
            case "naver" -> {
                body.put("client_id", Collections.singletonList(naverClientId));
                body.put("client_secret", Collections.singletonList(naverClientSecret));
            }
        }

        return body;
    }

    private static String getOAuthURL(String target) {
        return switch (target) {
            case "kakao" -> kakaoURL;
            case "google" -> googleURL;
            case "naver" -> naverURL;
            default -> null;
        };
    }

    private static WebClient getWebClient(String targetURI) {
        return WebClient.builder().
                defaultHeader("Content-type" , "application/x-www-form-urlencoded;charset=utf-8")
                .baseUrl(targetURI)
                .build();
    }
}
