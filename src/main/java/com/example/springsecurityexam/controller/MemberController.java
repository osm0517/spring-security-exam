package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/login")
    public String login(Model model){
        return "login";
    }

    @PostMapping("/login-proc")
    public String loginProcess(Model model){
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model){
        return "signup";
    }

    @PostMapping("/signup-proc")
    public String signupProcess(Model model){
        try{
//            input check
//            given
            String userId = Objects.requireNonNull(model.getAttribute("userId")).toString();
            String password = Objects.requireNonNull(model.getAttribute("password")).toString();
            String name = Objects.requireNonNull(model.getAttribute("name")).toString();

            Member member = new Member(userId, password, name, RoleType.USER);



        } catch (NullPointerException e){
            model.addAttribute("result", "fail");
        }
//        then
        return "signup-result";
    }
}
