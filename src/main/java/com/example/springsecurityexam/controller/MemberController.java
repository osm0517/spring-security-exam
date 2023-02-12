package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.config.utils.CookieUtils;
import com.example.springsecurityexam.domain.BuyItem;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.dto.member.*;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

//    --- view path start ---

    private static final String signupPath = "member/signup/signup";
    private static final String signupResultPath = "member/signup/signup-result";
    private static final String signupFailPath = "member/signup/signup-fail";
    private static final String loginPath = "member/login/login";
    private static final String profilePath = "member/member/profile";
    private static final String producerItemsPath = "items/myProducingItems";
    private static final String profileEditPath = "member/member/editForm";
    private static final String buyItemsPath = "member/consume/items";
    private static final String deleteFormPath = "member/member/deleteForm";
    private static final String deleteOAuthPath = "member/member/deleteOAuth";

//    --- view path end ---

    private final MemberService memberService;

    private final CookieUtils cookieUtils;

    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;

    /**
     * controller 역할
     * - 요청을 받고 응답을 함
     * - 적절한 요청인지 확인함
     */

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) boolean result,
            Model model
    ){
        log.debug("login form render");

        model.addAttribute("result", result);
        model.addAttribute("member", new LoginDto());

        return loginPath;
    }

    @GetMapping("/profile")
    public String profile(
            Model model,
            Principal principal
    ){
        log.debug("profile view render");

        String userId = principal.getName();

        Member user = memberService.checkUserId(userId);

        model.addAttribute("user", user);

        return profilePath;
    }

    @GetMapping("/profile/edit")
    public String profileEditForm(
            Model model,
            Principal principal
    ){
        log.debug("profile edit form render");

        String userId = principal.getName();

        Member user = memberService.checkUserId(userId);

        model.addAttribute("dto", new UserInfoEditDto(user.getName(), user.getEmail()));

        return profileEditPath;
    }

    /**
     * 정보 수정 로직을 처리
     */
    @PostMapping("/profile/edit")
    public String profileEdit(
            @Validated @ModelAttribute(name = "dto") UserInfoEditDto dto,
            BindingResult bindingResult,
            Principal principal
    ){

        if(bindingResult.hasErrors()){
            log.debug("error = {}", bindingResult);
            return profileEditPath;
        }

        String userId = principal.getName();

        memberService.updateUserInfo(userId, dto);

        log.debug("name = {}, email = {}", dto.getName(), dto.getEmail());

        return "redirect:/profile";
    }

//    @GetMapping("/profile/edit/password/popup")
//    public String passwordEditForm(
//            Model model
//    ){
//        log.debug("password edit form render");
//
//        model.addAttribute("dto", new PasswordEditDto());
//
//        return passwordEditPopupPath;
//    }
//
//    /**
//     * password를 수정함
//     * 수정할 때에 팝업창을 수정 후에 창이 닫혀서 리다이렉트는 안하는 걸로
//     */
//    @PostMapping("/profile/edit/password/popup")
//    public void passwordEdit(
//            @Validated @ModelAttribute PasswordEditDto dto,
//            BindingResult bindingResult,
//            @SessionAttribute(name = SessionUtils.session_login_id) long userId
//    ){
//        log.debug("popup debug");
//
//        if(!Objects.equals(dto.getPassword(), dto.getConfirm())){
//            bindingResult.reject("passwordNotEquals");
//        }
//
//        if(bindingResult.hasErrors()){
////            return passwordEditPopupPath;
//        }
//
//        try {
//            memberService.updatePassword(userId, dto);
//        }catch(IllegalArgumentException e){
//            log.warn("not match");
//        }
//    }

    @GetMapping("/profile/consume/items")
    public String buyItemsForm(
            Model model,
            Principal principal
    ){
        log.debug("buyItemsView form render");

        String userId = principal.getName();

        List<BuyItem> buyItems = memberService.findBuyItems(userId);

        model.addAttribute("items", buyItems);

        return buyItemsPath;
    }

    @GetMapping("/member/delete")
    public String deleteFrom(
            Model model,
            Principal principal
    ){

        String userId = principal.getName();

        Member member = memberService.checkUserId(userId);
        model.addAttribute("dto", new DeleteMemberDto());

        if(member.getUserId().contains("_")){
            return deleteOAuthPath;
        }

        return deleteFormPath;
    }

    @PostMapping("/member/delete/{type}")
    public String deleteMember(
            @Validated @ModelAttribute(name = "dto") DeleteMemberDto dto,
            BindingResult bindingResult,
            @PathVariable String type,
            Principal principal
    ){
        log.debug("type = {}", type);

        if(bindingResult.hasErrors()){
            log.debug("error = {}", bindingResult);
            switch (type){
                case "form" -> {
                    return deleteFormPath;
                }
                case "oauth" -> {
                    return deleteOAuthPath;
                }
                default -> log.error("not exist type");
            }
        }

        String userId = principal.getName();

        memberService.delete(type, userId, dto.getValue());

        return "redirect:/";
    }

    @GetMapping("/profile/produce/items")
    public String produceItems(
            Model model,
            Principal principal
    ){
        log.debug("produceItems view render");

        String userId = principal.getName();

        Member member = memberService.checkUserId(userId);

        model.addAttribute("items", member.getItems());

        return producerItemsPath;
    }

    @GetMapping("/signup")
    public String signup(
            Model model
    ){
        log.info("signup view render");

        model.addAttribute("member", new MemberSaveDto());

        return signupPath;
    }

    @PostMapping("/signup")
    public String signupProcess(
            @Validated @ModelAttribute(name = "member") MemberSaveDto dto,
            BindingResult bindingResult,
            Model model
    ){
        if(!Objects.equals(dto.getPassword(), dto.getPasswordConfirm())){
            bindingResult.reject("isSamePassword", "비밀번호가 동일하지 않습니다.");
        }
        if(bindingResult.hasErrors()){
            log.debug("error = {}", bindingResult);
            return signupPath;
        }
//            given
        Member member = new Member(dto.getUserId(), dto.getPassword(), dto.getName(), dto.getEmail(), RoleType.USER);
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
