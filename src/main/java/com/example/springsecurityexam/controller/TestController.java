package com.example.springsecurityexam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/login")
    public String login(Model model){
        return "login";
    }
    @GetMapping("/signup")
    public String signup(Model model){
        return "signup";
    }
    @GetMapping("/list")
    public String list(Model model){
        return "list";
    }
    @GetMapping("/send")
    public String send(Model model){
        return "send";
    }
}
