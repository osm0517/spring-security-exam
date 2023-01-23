package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.config.utils.SessionUtils;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;

    @GetMapping("/")
    public String homeView(
            Model model,
            HttpServletRequest request,
            @SessionAttribute(name = SessionUtils.session_login_id, required = false) Integer userId
    ){

        log.debug("home");

        if(userId == null){
            log.debug("not login");
            return "index";
        }

        Member member = memberService.checkSession(userId);

        log.debug("userId = {}", userId);
        log.debug("member = {}", member);

        HttpSession h = request.getSession(false);
        h.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.debug("attributes = {}", h.getAttribute(name)));

        if(member == null){
            log.debug("not login");
            return "index";
        }

        model.addAttribute("user", member);

        return "loginHome";
    }
}