package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.config.JWTConfig;
import com.example.springsecurityexam.config.utils.CookieUtils;
import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@Slf4j
public class MemberController {

//    --- view path start ---

    private final String signupPath = "/member/signup/signup";
    private final String signupResultPath = "/member/signup/signup-result";
    private final String signupFailPath = "/member/signup/signup-fail";
    private final String loginPath = "/member/login/login";
    private final String loginFailPath = "/member/login/login-fail";

//    --- view path end ---

    private MemberService memberService;

    private JWTConfig jwtConfig;

    private CookieUtils cookieUtils;

    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

//    @Value("${jwt.access_expire_time}")
    private long accessExpireTime = 1800000;

//    @Value("${jwt.refresh_expire_time}")
    private long refreshExpireTime = 1209600000;

    public MemberController(MemberService memberService, JWTConfig jwtConfig, CookieUtils cookieUtils){
        this.jwtConfig = jwtConfig;
        this.memberService = memberService;
        this.cookieUtils = cookieUtils;
    }

    @GetMapping("/login")
    public String login(){
        log.debug("login view render");
        return loginPath;
    }

//    @GetMapping("/logout")
//    public String logout(
//            HttpServletResponse response
//    ){
//        log.debug("user logout");
//
//        response.addCookie(cookieUtils.setCookie(refreshTokenName, null, 0));
//        response.addCookie(cookieUtils.setCookie(accessTokenName, null, 0));
//
//        return "redirect:/";
//    }

    @GetMapping("/test")
    public void test(){
        final String kakaoUri = "https://kauth.kakao.com/oauth/authorize?client_id="+
                clientId+
                "&redirect_uri="+"http://localhost:8080/login/oauth2/code/kakao"+
                "&response_type=code";
        RestTemplate rt = new RestTemplate();
        String result = rt.getForObject(kakaoUri, String.class);
        log.debug("인가코드 받기 위한 get");
        log.debug("result = {}", result);
    }

    @RequestMapping("/code/kakao")
    public void oauthKakao(
            String code,
            HttpServletResponse response
    ){
        log.debug("login oauth2 redirect uri");
        log.debug("client id = {}", clientId);
        log.debug("code = {}", code);

        final String kakaoUri = "https://kauth.kakao.com";

        WebClient client = WebClient.builder().
                defaultHeader("Content-type" , "application/x-www-form-urlencoded;charset=utf-8")
                .baseUrl(kakaoUri)
                .build();

        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.put("grant_type", Collections.singletonList("authorization_code"));
        request.put("client_id", Collections.singletonList(clientId));
        request.put("redirect_uri", Collections.singletonList("http://localhost:8080/code/kakao"));
        request.put("code", Collections.singletonList(code));

        Mono<JSONObject> results = client.post()
                .uri("/oauth/token")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(JSONObject.class);

        results.subscribe(result -> {
            log.debug("post result = {}", result);
        });

        log.debug("token 받아보자잇");
        log.debug("results = {}", results);
    }

    @PostMapping("/test")
    @ResponseBody
    public void postTest(@RequestBody String data){
        System.out.println("data = " + data);
        log.debug("data send test result = {}", data);
        log.debug("ajax test");

    }

    @PostMapping("/login")
    public String loginProcess(
            String userId,
            String password,
            HttpServletRequest request,
            HttpServletResponse response
    ){
//        log.debug("error test");
        if(parameterNullCheck(request)){
            return loginFailPath;
        } else {
            Member member = new Member(userId, password, null, null, null);

            Member loginResult = memberService.login(member);
            if (loginResult != null) {
                log.debug("login success");
                response.addCookie(cookieUtils.setCookie(
                        refreshTokenName,
                        jwtConfig.createRefreshToken(userId),
                        refreshExpireTime
                        ));
                return "redirect:/";
            }
            log.debug("login fail");
            return "redirect:/login";
        }
    }

    @GetMapping("/signup")
    public String signup(){
        log.info("signup view render");
        return signupPath;
    }

    @PostMapping("/signup")
    public String signupProcess(
            @RequestParam("userId") String userId,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            Model model,
            HttpServletRequest request
    ){
//        input check
        if(parameterNullCheck(request)){
            model.addAttribute("result", "fail");
            return signupResultPath;
        }else {
//            given
            Member member = new Member(userId, password, name, email, RoleType.USER);
//            when
            if(memberService.signup(member)){
//            then
                model.addAttribute("result", "success");
                return signupResultPath;
            }else{
                return signupFailPath;
            }
        }
    }

    /**
     * 입력한 파라미터 중에서 null이 존재하는지 확인함
     * null이 존재하면 true 없으면 false
     * @param request
     * @return
     */
    private static boolean parameterNullCheck(HttpServletRequest request) {
        AtomicBoolean result = new AtomicBoolean();
        request.getParameterNames().asIterator()
                .forEachRemaining(param -> {
                    if(request.getParameter(param) == null){
                        log.warn("parameterNull");
                        result.set(true);
                    }
                });
        return result.get();
    }
}
