package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}
