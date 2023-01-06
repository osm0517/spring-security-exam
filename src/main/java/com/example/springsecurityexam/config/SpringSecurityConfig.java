package com.example.springsecurityexam.config;

import com.example.springsecurityexam.auth.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;


    // 암호화에 사용하는 class를 bean으로 등록해서 사용
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                    .authorizeHttpRequests()
                    .anyRequest().permitAll()
//                    .anyRequest().authenticated()
                .and()
                    .csrf().disable()
                    .formLogin().disable()
//                    .loginPage("/login").permitAll()
//                    .loginProcessingUrl("/login-proc")
//                .and()
                .oauth2Login()
                .loginPage("/login")
//                .userInfoEndpoint()
//                .userService(customOAuth2UserService)
        ;
        return http.build();
    }
}
