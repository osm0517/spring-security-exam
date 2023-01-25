package com.example.springsecurityexam.auth.userDetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserDetails extends UserDetails, OAuth2User {
}
