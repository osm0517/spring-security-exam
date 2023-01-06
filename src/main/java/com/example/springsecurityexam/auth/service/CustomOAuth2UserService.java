package com.example.springsecurityexam.auth.service;

import com.example.springsecurityexam.auth.OAuth2UserInfo;
import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.repository.JPAMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    BCryptPasswordEncoder encoder;

    @Autowired
    JPAMemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        System.out.println("userRequest = " + userRequest);

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();    //kakao
        String providerId = oAuth2User.getAttribute("id");
        String userId = provider+"_"+providerId;  			// 사용자가 입력한 적은 없지만 만들어준다

        String uuid = UUID.randomUUID().toString().substring(0, 6);
        String password = encoder.encode("패스워드"+uuid);  // 사용자가 입력한 적은 없지만 만들어준다

        String name = oAuth2User.getAttribute("nickname");
        String email = oAuth2User.getAttribute("email");
        RoleType role = RoleType.USER;

        Member member = memberRepository.findByUserId(userId);

        //DB에 없는 사용자라면 회원가입처리
        if(member == null){
            member = new Member(userId, password, name, role);
            memberRepository.save(member);
        }

        return new OAuth2UserDetails(member, oAuth2User.getAttributes());
    }

}
