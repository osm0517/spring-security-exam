package com.example.springsecurityexam.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/oauth2/authorize")
@Slf4j
public class OAuth2AuthorizationController {

    @GetMapping("/{target}")
    public void kakaoAuthorize(
            @PathVariable String target
    ){
        log.debug("authorize target = {}", target);
    }
}
