package com.example.springsecurityexam.auth.service;

import com.example.springsecurityexam.auth.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Objects;


public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    OAuth2User oAuth2User;

    public KakaoOAuth2UserInfo(OAuth2User oAuth2User){
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public String getProviderId() {
        return Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> accountMap = oAuth2User.getAttribute("kakao_account");
        return Objects.requireNonNull(accountMap).get("email").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> accountMap = oAuth2User.getAttribute("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) accountMap.get("profile");
        return Objects.requireNonNull(profile).get("nickname").toString();
    }
}
