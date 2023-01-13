package com.example.springsecurityexam.controller;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Controller
@Slf4j
public class OAuth2GoogleLoginController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @GetMapping(value = "/login/oauth2/code/google")
    public void googleLogin(
            @PathVariable String code
    ){

        WebClient client = WebClient.builder().
                defaultHeader("Content-type" , "application/x-www-form-urlencoded;charset=utf-8")
                .baseUrl("https://apitest.acme.com")
                .build();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.put("grant_type", Collections.singletonList("authorization_code"));
        body.put("redirect_uri", Collections.singletonList("http://localhost:8080/login/oauth2/code/google"));
        body.put("code", Collections.singletonList(code));
        body.put("client_id", Collections.singletonList(googleClientId));
        body.put("client_secret", Collections.singletonList(googleClientSecret));

        JSONObject results = client.post()
                .uri("/oauth/token")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

//        results 가 null 인 것처럼 오류가 발생하면 처리를 해줄 페이지 제작하면 대체하기
        if(results == null){
            log.debug("google login results null");
        }

        log.debug("results = {}", results);

    }

//    private MultiValueMap<String, String> getBody(String code, String target) {
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//
//        body.put("grant_type", Collections.singletonList("authorization_code"));
//        body.put("redirect_uri", Collections.singletonList("http://localhost:8080/code/"+target));
//        body.put("code", Collections.singletonList(code));
//
//        switch (target){
//            case "kakao" -> {
//                body.put("client_id", Collections.singletonList(kakaoClientId));
//            }
//            case "google" -> {
//                body.put("client_id", Collections.singletonList(googleClientId));
//                body.put("client_secret", Collections.singletonList(googleClientSecret));
//            }
//            case "naver" -> {
//                body.put("client_id", Collections.singletonList(naverClientId));
//                body.put("client_secret", Collections.singletonList(naverClientSecret));
//            }
//        }
//
//        return body;
//    }
}
