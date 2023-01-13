package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.config.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
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
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Controller
@Slf4j
public class OAuth2LoginController {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;

//    ----- base URL start -----

    private static final String kakaoURL = "https://kauth.kakao.com";

    private static final String googleURL = "dd";

    private static final String naverURL = "dd";

//    ----- base URL end -----

    private CookieUtils cookieUtils;

    public OAuth2LoginController(CookieUtils cookieUtils){
        this.cookieUtils = cookieUtils;
    }

    @RequestMapping("/code/{target}")
    public String oauthKakao(
            String code,
            @PathVariable String target,
            HttpServletResponse response
    ){
        log.debug("login oauth2 redirect uri");

        String tarterURL =  getOAuthURL(target);

        if(tarterURL == null){
            log.error("wrong query string : name = {}", target);
        }

        WebClient client = getWebClient(tarterURL);

        MultiValueMap<String, String> body = getBody(code);

        JSONObject results = client.post()
                .uri("/oauth/token")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

        String accessTokenValue = results.get("access_token").toString();
        String refreshTokenValue = results.get("refresh_token").toString();
        long accessTokenExpireTime = Long.parseLong(results.get("expires_in").toString());
        long refreshTokenExpireTime = Long.parseLong(results.get("refresh_token_expires_in").toString());

//        AtomicReference<String> accessTokenValue = new AtomicReference<>("");
//        AtomicReference<String> refreshTokenValue = new AtomicReference<>("");
//
//        AtomicReference<String> accessTokenExpireTime = new AtomicReference<>("");
//        AtomicReference<String> refreshTokenExpireTime = new AtomicReference<>("");
//
//        results.subscribe(result -> {
//            accessTokenValue.set(result.get("access_token").toString());
//            refreshTokenValue.set(result.get("refresh_token").toString());
//
//            accessTokenExpireTime.set(result.get("expires_in").toString());
//            refreshTokenExpireTime.set(result.get("refresh_token_expires_in").toString());
//        });
//
        log.debug("debug = {}, {}, {}, {}", accessTokenValue, accessTokenExpireTime, refreshTokenValue, refreshTokenExpireTime);

        response.addCookie(cookieUtils.setCookie(accessTokenName,
                accessTokenValue,
                accessTokenExpireTime));
        response.addCookie(cookieUtils.setCookie(refreshTokenName,
                refreshTokenValue,
                refreshTokenExpireTime));

        return "redirect:/";

    }

    private MultiValueMap<String, String> getBody(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.put("grant_type", Collections.singletonList("authorization_code"));
        body.put("client_id", Collections.singletonList(clientId));
        body.put("redirect_uri", Collections.singletonList("http://localhost:8080/code/kakao"));
        body.put("code", Collections.singletonList(code));

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

    private static WebClient getWebClient(String kakaoUri) {
        WebClient client = WebClient.builder().
                defaultHeader("Content-type" , "application/x-www-form-urlencoded;charset=utf-8")
                .baseUrl(kakaoUri)
                .build();
        return client;
    }
}
