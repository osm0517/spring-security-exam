package com.example.springsecurityexam.config;

import com.example.springsecurityexam.auth.service.CustomOAuth2UserService;
import com.example.springsecurityexam.config.utils.CookieUtils;
import com.example.springsecurityexam.config.utils.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfig {

    private CustomOAuth2UserService customOAuth2UserService;

    private JWTConfig jwtConfig;

    private CookieUtils cookieUtils;

    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;

    public SpringSecurityConfig(CustomOAuth2UserService customOAuth2UserService, JWTConfig jwtConfig, CookieUtils cookieUtils){
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtConfig = jwtConfig;
        this.cookieUtils = cookieUtils;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                    .authorizeHttpRequests()
                    .anyRequest().permitAll()
//                    .anyRequest().authenticated()
                .and()
                    .csrf().disable()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .formLogin().disable()
                    .addFilterBefore(new JwtFilter(jwtConfig, cookieUtils), UsernamePasswordAuthenticationFilter.class)
//                    .loginPage("/login").permitAll()
//                    .loginProcessingUrl("/login-proc")
//                .and()
                    .logout()
                        .logoutUrl("/logout")
                        .deleteCookies(accessTokenName, refreshTokenName)
                        .logoutSuccessUrl("/")
                .and()
                    .oauth2Login()
                        .loginPage("/login")
                    .authorizationEndpoint()
                        .baseUri("/oauth2/authorize")
                .and()
                    .userInfoEndpoint()
                        .userService(customOAuth2UserService)
        ;
        return http.build();
    }
}
