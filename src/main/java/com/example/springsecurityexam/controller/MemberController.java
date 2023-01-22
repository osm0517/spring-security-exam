package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.config.JWTConfig;
import com.example.springsecurityexam.config.utils.CookieUtils;
import com.example.springsecurityexam.config.utils.SessionUtils;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    private final String loginSuccessPath = "/loginHome";

//    --- view path end ---

    private MemberService memberService;

    private JWTConfig jwtConfig;

    private CookieUtils cookieUtils;

    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;


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

    @GetMapping("/login/fail")
    public String loginFail(){
        log.debug("login fail");
        return loginFailPath;
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

    @PostMapping("/login")
    public String loginProcess(
            String userId,
            String password,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session
    ){
//        log.debug("error test");
        if(parameterNullCheck(request)){
            return "redirect:/login/fail";
        } else {
            Member member = new Member(userId, password, null, null, null);

            Member loginResult = memberService.login(member);
            if (loginResult != null) {
                log.debug("login success");
//                response.addCookie(cookieUtils.setCookie(
//                        refreshTokenName,
//                        jwtConfig.createRefreshToken(userId),
//                        refreshExpireTime
//                        ));

                HttpSession requestSession = request.getSession();
                requestSession.setAttribute(SessionUtils.session_login_id, loginResult.getId());
                requestSession.setAttribute(SessionUtils.session_login_username, loginResult.getName());

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
