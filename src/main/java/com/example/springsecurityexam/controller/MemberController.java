package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
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

    private final String signupPath = "/member/signup";
    private final String signupResultPath = "/member/signup-result";
    private final String signupFailPath = "/member/signup-fail";
    private final String loginPath = "/member/login";

//    --- view path end ---

    @Autowired
    private MemberService memberService;

    @GetMapping("/login")
    public String login(Model model){
        log.info("login view render");
        return loginPath;
    }

    @PostMapping("/login")
    public String loginProcess(Model model){
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model){
        log.info("signup view render");
        return signupPath;
    }

    @PostMapping("/signup")
    public String signupProcess(
            @RequestParam("userId") String userId,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            Model model,
            HttpServletRequest request
    ){
//        input check
        if(parameterNullCheck(request)){
            model.addAttribute("result", "fail");
            return signupResultPath;
        }else {
//            given
            Member member = new Member(userId, password, name, RoleType.USER);
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
     * @param request
     * @return
     */
    private static boolean parameterNullCheck(HttpServletRequest request) {
        AtomicBoolean result = new AtomicBoolean();
        request.getParameterNames().asIterator()
                .forEachRemaining(param -> {
                    if(request.getParameter(param) == null){
                        result.set(true);
                    }
                });
        return result.get();
    }
}
