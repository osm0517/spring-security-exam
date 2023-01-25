package com.example.springsecurityexam.auth;

import com.example.springsecurityexam.auth.service.UserDetailsServiceImpl;
import com.example.springsecurityexam.auth.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * npe, usernameNotFoundException, BadCredentialsException 처리해줘야함
     */

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userId = authentication.getName();
        String password = authentication.getCredentials().toString();

        if(userId == null){
            throw new NullPointerException("userId null");
        }
        CustomUserDetails user = userDetailsService.loadUserByUsername(userId);
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadCredentialsException("credential not match");
        }
        log.debug("authenticate success");
        return new UsernamePasswordAuthenticationToken(user.getId(), password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.debug("provider");
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
