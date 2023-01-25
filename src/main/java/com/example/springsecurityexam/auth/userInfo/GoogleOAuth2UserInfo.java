package com.example.springsecurityexam.auth.userInfo;

import com.example.springsecurityexam.auth.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Objects;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    OAuth2User oAuth2User;

    public GoogleOAuth2UserInfo(OAuth2User oAuth2User){
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public String getProviderId() {
        return Objects.requireNonNull(oAuth2User.getAttribute("sub")).toString();
    }

    @Override
    public String getEmail() {
        return Objects.requireNonNull(oAuth2User.getAttribute("email")).toString();
    }

    @Override
    public String getName() {
        return Objects.requireNonNull(oAuth2User.getAttribute("name")).toString();
    }
}
