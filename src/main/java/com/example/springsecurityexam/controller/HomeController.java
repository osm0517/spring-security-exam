package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.config.JWTConfig;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;
    private final JWTConfig jwtConfig;

    @GetMapping("/")
    public String homeView(
            Model model,
            Principal principal
    ) {

        if(principal == null){
            return "index";
        }

        log.debug("username = {}", principal.getName());
        String username = principal.getName();
        Member member = memberService.checkUsername(username);

        model.addAttribute("user", member);
        return "loginHome";

    }
}
