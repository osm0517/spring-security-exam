package com.example.springsecurityexam.auth.userInfo;

import com.example.springsecurityexam.auth.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    OAuth2User oAuth2User;

    Map<String, Object> attributesMap;

    public NaverOAuth2UserInfo(OAuth2User oAuth2User){
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public String getProviderId() {
        attributesMap = oAuth2User.getAttribute("response");
        if (attributesMap == null){
            log.debug("naver oAuth2 login response map null");
            return UUID.randomUUID().toString().substring(0, 6);
        }
        return attributesMap.get("id").toString();
    }

    @Override
    public String getEmail() {
        attributesMap = oAuth2User.getAttribute("response");
        if (attributesMap == null){
            log.debug("naver oAuth2 login response map null");
            return "defaultEmail";
        }
        return attributesMap.get("email").toString();
    }

    @Override
    public String getName() {
        attributesMap = oAuth2User.getAttribute("response");
        if (attributesMap == null){
            log.debug("naver oAuth2 login response map null");
            return "defaultName";
        }
        return attributesMap.get("name").toString();
    }
}
