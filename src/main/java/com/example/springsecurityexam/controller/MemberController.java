package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

//    @GetMapping("/login")
//    public String login(Model model){
//        return "login";
//    }
}
