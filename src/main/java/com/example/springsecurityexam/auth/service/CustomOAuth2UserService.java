package com.example.springsecurityexam.auth.service;

import com.example.springsecurityexam.auth.OAuth2UserInfo;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    public CustomOAuth2UserService(BCryptPasswordEncoder encoder, MemberRepository memberRepository){
        this.encoder = encoder;
        this.memberRepository = memberRepository;
    }
    BCryptPasswordEncoder encoder;

    MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.debug("oauth2 loadUser");

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String uri = userRequest.getClientRegistration().getRegistrationId();
        OAuth2AccessToken token = userRequest.getAccessToken();

//        ----- parameter 가 다 잘 들어왔는지 확인 start -----
        log.debug("oauth2 token = {}", token);
        log.debug("access token = {}", userRequest.getAccessToken().toString());
        userRequest.getAdditionalParameters().forEach(
                (key, value) -> log.debug("key = {}, value = {}", key, value)
        );
        log.debug("token value = {}",userRequest.getAccessToken().getTokenValue());
        log.debug("scope = {}", userRequest.getAccessToken().getScopes().toString());
//        ----- parameter 가 다 잘 들어왔는지 확인 end -----

        OAuth2UserInfo userInfo = setUserInfo(uri, oAuth2User);
//        기본적으로 저장되는 정보
        String email = "defaultEmail";
        String name = "defaultName";

//        attributes 로 어떤 값이 넘어오는지 확인
//        attributesCheckSout(userInfo.getAttributes());

        String providerId = userInfo.getProviderId(); // 임의의 숫자배열로 이루어진 id
        String userId = uri + "_" + providerId;  	  // 사용자가 입력한 적은 없지만 만들어준다

        String uuid = UUID.randomUUID().toString().substring(0, 6);
        String password = encoder.encode("password" + uuid); // 사용자가 입력한 적은 없지만 만들어준다

        try {

            email = userInfo.getEmail();
            name = userInfo.getName();

        }catch (NullPointerException e){
            log.error("email or nickname null \n email = {}, nickname = {}", email, name);
        }

        RoleType role = RoleType.USER;

        Member member = memberRepository.findByUserId(userId);

        //DB에 없는 사용자라면 회원가입처리
        if(member == null){
            log.debug("check to member constructor parameter = {} {} {} {} {}", userId, password, name, email, role);
            member = new Member(userId, password, name, email, role);
            memberRepository.save(member);
        }
        OAuth2UserDetails userDetails = new OAuth2UserDetails(member, oAuth2User.getAttributes());

        return new OAuth2UserDetails(member, oAuth2User.getAttributes());
    }

    /**
     * uri를 체크해서 처리해줄 수 있는 handler를 return 해줌
     * @param uri
     * @return OAuth2UserInfo
     */
    private static OAuth2UserInfo setUserInfo(String uri, OAuth2User oAuth2User) {
        OAuth2UserInfo userInfo;

        log.debug("OAuth2UserInfo uri = {}", uri);

        if (Objects.equals(uri, "kakao")){
            userInfo = new KakaoOAuth2UserInfo(oAuth2User);
        } else if (Objects.equals(uri, "google")) {
            userInfo = new GoogleOAuth2UserInfo(oAuth2User);
        }else {
            userInfo = new NaverOAuth2UserInfo(oAuth2User);
        }
        return userInfo;
    }

    /**
     * attributes 에 어떤 정보가 들어오고 어떻게 사용을 할지를 판단하기 위해서 사용
     * @param attributes
     */
    private static void attributesCheckSout(Map<String, Object> attributes) {
        attributes.forEach((name, value) -> {
            log.debug(name + "," + value);
        });
    }

}
