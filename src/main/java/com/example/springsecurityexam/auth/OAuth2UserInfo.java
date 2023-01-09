package com.example.springsecurityexam.auth;

import java.util.Map;

public interface OAuth2UserInfo{
    Map<String, Object> getAttributes();
    String getProviderId();
    String getEmail();
    String getName();
}
