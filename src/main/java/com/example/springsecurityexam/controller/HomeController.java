package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.config.JWTConfig;
import com.example.springsecurityexam.config.utils.SessionUtils;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.service.MemberService;
import io.netty.util.internal.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.security.Principal;
import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;
    private final JWTConfig jwtConfig;

    @GetMapping("/")
    public String homeView(
            Model model,
            HttpServletRequest request,
            Principal principal
    ) {

        log.debug("username = {}", principal.getName());
        String username = principal.getName();
        Member member = memberService.checkUsername(username);

        if(member == null){
            return "index";
        }
        model.addAttribute("user", member);
        return "loginHome";

//        if(userId == null){
//            log.debug("not login");
//            return "index";
//        }
//
//        try {
//            Member member = memberService.checkSession(userId);
//
//            log.debug("userId = {}", userId);
//            log.debug("member = {}", member);
//
//            HttpSession h = request.getSession(false);
//            h.getAttributeNames().asIterator()
//                    .forEachRemaining(name -> log.debug("attributes = {}", h.getAttribute(name)));
//
//            if (member == null) {
//                log.debug("not login");
//                return "index";
//            }
//
//            model.addAttribute("user", member);
//            return "loginHome";
//        }catch (NoSuchElementException e){
//            log.debug("delete member");
//            return "index";
//        }
//    }
    }

    @GetMapping("/login/success")
    public String loginSuccess(
//            Model model,
//            Principal principal
            Authentication authentication
    ) {
        log.debug("login success = {}", authentication);

//        String username = principal.getName();
//        log.debug("username = {}", username);
//        Member member = memberService.checkUsername(username);
//        model.addAttribute("user", member);

        return "redirect:/";
    }
}
