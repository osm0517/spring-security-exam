package com.example.springsecurityexam.auth.service;

import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.repository.JPAMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    public CustomOAuth2UserService(JPAMemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
    BCryptPasswordEncoder encoder;

    JPAMemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.debug("oauth2 loadUser");

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        attributes.forEach((name, value) -> {
            System.out.println(name + "," + value);
        });

        String providerId = Objects.requireNonNull(oAuth2User.getAttribute("id")).toString(); // 임의의 숫자배열로 이루어진 id
        String userId = "kakao_"+providerId;  			// 사용자가 입력한 적은 없지만 만들어준다

        String uuid = UUID.randomUUID().toString().substring(0, 6);
        String password = "";
        try {
            password = encoder.encode("password" + uuid); // 사용자가 입력한 적은 없지만 만들어준다

        }catch (NullPointerException e){
            log.error("password encode input null");
            log.warn("why password null..? => {}, {}", password, uuid);
            password = "testPassword";
        }
        String email = "defaultEmail";
        String name = "defaultName";
        try {
            Map<String, Object> accountMap = oAuth2User.getAttribute("kakao_account");
            email = accountMap.get("email").toString();
            Map<String, Object> profile = (Map<String, Object>) accountMap.get("profile");
            name = profile.get("nickname").toString();
        }catch (NullPointerException e){
            log.error("email or nickname null \n email = {}, nickname = {}", email, name);
        }

        RoleType role = RoleType.USER;
//
        Member member = memberRepository.findByUserId(userId);

        //DB에 없는 사용자라면 회원가입처리
        if(member == null){
            log.warn("check to member constructor parameter = {} {} {} {} {}", userId, password, name, email, role);
            member = new Member(userId, password, name, email, role);
            memberRepository.save(member);
        }

        return new OAuth2UserDetails(member, oAuth2User.getAttributes());
    }

}
