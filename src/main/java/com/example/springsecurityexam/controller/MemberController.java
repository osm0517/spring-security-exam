package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String login(){
        log.debug("login view render");
        return loginPath;
    }

    @GetMapping("/test")
    public void test(){
        log.debug("test Debug");
        log.info("test Debug");
        log.warn("test Debug");
        log.error("test Debug");
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
            @RequestParam("userId") String userId,
            @RequestParam("password") String password,
            HttpServletRequest request
    ){
//        log.debug("error test");
        if(parameterNullCheck(request)){
            return loginFailPath;
        } else {
            Member member = new Member(userId, password, null, null, null);

            if(memberService.login(member)){
                log.debug("login success");
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
