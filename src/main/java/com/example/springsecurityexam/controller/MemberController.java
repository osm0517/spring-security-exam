package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.config.JWTConfig;
import com.example.springsecurityexam.config.utils.CookieUtils;
import com.example.springsecurityexam.config.utils.SessionUtils;
import com.example.springsecurityexam.domain.BuyItem;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.dto.member.PasswordEditDto;
import com.example.springsecurityexam.dto.member.UserInfoEditDto;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

//    --- view path start ---

    private static final String signupPath = "/member/signup/signup";
    private static final String signupResultPath = "/member/signup/signup-result";
    private static final String signupFailPath = "/member/signup/signup-fail";
    private static final String loginPath = "/member/login/login";
    private static final String loginFailPath = "/member/login/login-fail";

    private static final String loginSuccessPath = "/loginHome";
    private static final String profilePath = "/member/member/profile";
    private static final String producerItemsPath = "/items/producerItems";
    private static final String profileEditPath = "/member/member/editForm";
    private static final String passwordEditPopupPath = "/member/popup/passwordEdit";
    private static final String buyItemsPath = "/member/consume/items";

//    --- view path end ---

    private final MemberService memberService;

    private final JWTConfig jwtConfig;

    private final CookieUtils cookieUtils;

    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;


    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) boolean result,
            Model model
    ){
        log.debug("login view render");

        model.addAttribute("result", result);

        return loginPath;
    }

    @GetMapping("/profile")
    public String profile(
            Model model,
            @SessionAttribute(name = SessionUtils.session_login_id) long userId
    ){
        log.debug("profile view render");

        Member user = memberService.checkSession(userId);

        model.addAttribute("user", user);

        return profilePath;
    }

    @GetMapping("/profile/edit")
    public String profileEditForm(
            Model model,
            @SessionAttribute(name = SessionUtils.session_login_id) long userId
    ){
        log.debug("profile edit form render");

        Member user = memberService.checkSession(userId);

        model.addAttribute("dto", new UserInfoEditDto(user.getName(), user.getEmail()));

        return profileEditPath;
    }

    @GetMapping("/profile/edit/password/popup")
    public String passwordEditForm(
            Model model,
            @SessionAttribute(name = SessionUtils.session_login_id) long userId
    ){
        log.debug("password edit form render");

        model.addAttribute("dto", new PasswordEditDto());

        return passwordEditPopupPath;
    }

    @GetMapping("/profile/consume/items")
    public String buyItemsForm(
            Model model,
            @SessionAttribute(name = SessionUtils.session_login_id) long userId
    ){
        log.debug("buyItemsView form render");

        List<BuyItem> buyItems = memberService.findBuyItems(userId);

        model.addAttribute("items", buyItems);

        return buyItemsPath;
    }

    @GetMapping("/login/fail")
    public String loginFail(){
        log.debug("login fail");
        return loginFailPath;
    }

    @GetMapping("/login/success")
    public String loginSuccess(){
        log.debug("login success");
        return loginSuccessPath;
    }

    @GetMapping("/{userId}/items")
    public String produceItems(
            Model model,
            @PathVariable(name = "userId") long userId
    ){
        log.debug("produceItems view render");

        Member member = memberService.checkSession(userId);

        model.addAttribute("items", member.getItems());

        return producerItemsPath;
    }

    /**
     * 일단 로그아웃 로직으로 세션만 삭제하는 걸로 해놓음
     * 추후에 추가를 해야함
     */
    @GetMapping("/logout")
    public String logout(
            HttpServletRequest request
    ){
        log.debug("user logout");

        HttpSession session = request.getSession(false);
        session.invalidate();

        return "redirect:/";
    }

    /**
     * 정보 수정 로직을 처리
     */
    @PostMapping("/profile/edit")
    public String profileEdit(
            @ModelAttribute UserInfoEditDto dto,
            @SessionAttribute(name = SessionUtils.session_login_id) long userId
    ){

        Member member = memberService.updateUserInfo(userId, dto);

        log.debug("name = {}, email = {}", dto.getName(), dto.getEmail());

        return "redirect:/profile";
    }

    /**
     * password를 수정함
     * 수정할 때에 팝업창을 수정 후에 창이 닫혀서 리다이렉트는 안하는 걸로
     */
    @PostMapping("/profile/edit/password/popup")
    public void passwordEdit(
            @ModelAttribute PasswordEditDto dto,
            @SessionAttribute(name = SessionUtils.session_login_id) long userId
    ){
        log.debug("popup debug");
        try {
            memberService.updatePassword(userId, dto);
        }catch(IllegalArgumentException e){
            log.warn("not match");
        }
    }

    @PostMapping("/login")
    public String loginProcess(
            String userId,
            String password,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session
    ){
        log.debug("error test");
        if(parameterNullCheck(request)){
            return "redirect:/login/fail";
        } else {
            Member member = new Member(userId, password, null, null, null);

            Member loginResult = memberService.login(member);
            if (loginResult != null) {
                log.debug("login success");

                HttpSession requestSession = request.getSession();
                requestSession.setAttribute(SessionUtils.session_login_id, loginResult.getId());
                requestSession.setAttribute(SessionUtils.session_login_username, loginResult.getName());

                return "redirect:/";
            }

            log.debug("login fail");

            return "redirect:/login";
        }
    }

    @GetMapping("/signup")
    public String signup(){
        log.info("signup view render");
        return signupPath;
    }

    @PostMapping("/signup")
    public String signupProcess(
            @RequestParam("userId") String userId,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            Model model,
            HttpServletRequest request
    ){
//        input check
        if(parameterNullCheck(request)){
            model.addAttribute("result", "fail");
            return signupResultPath;
        }else {
//            given
            Member member = new Member(userId, password, name, email, RoleType.USER);
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
     * null이 존재하면 true 없으면 false
     */
    private static boolean parameterNullCheck(HttpServletRequest request) {
        AtomicBoolean result = new AtomicBoolean();
        request.getParameterNames().asIterator()
                .forEachRemaining(param -> {
                    if(request.getParameter(param) == null){
                        log.warn("parameterNull");
                        result.set(true);
                    }
                });
        return result.get();
    }
}
