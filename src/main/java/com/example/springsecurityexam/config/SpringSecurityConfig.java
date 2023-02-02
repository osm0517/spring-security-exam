package com.example.springsecurityexam.config;

import com.example.springsecurityexam.auth.handler.loginHandler.FailHandlerImpl;
import com.example.springsecurityexam.auth.handler.loginHandler.SuccessHandlerImpl;
import com.example.springsecurityexam.auth.service.CustomOAuth2UserService;
import com.example.springsecurityexam.auth.service.UserDetailsServiceImpl;
import com.example.springsecurityexam.config.utils.CookieUtils;
import com.example.springsecurityexam.config.utils.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {


    private final CustomOAuth2UserService customOAuth2UserService;
    private final JWTConfig jwtConfig;
    private final CookieUtils cookieUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final FailHandlerImpl failHandler;
    private final SuccessHandlerImpl successHandler;

    @Value("${jwt.access_token_name}")
    private String accessTokenName;

    @Value("${jwt.refresh_token_name}")
    private String refreshTokenName;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                    .authorizeHttpRequests()
                    .requestMatchers("/css/**", "/").permitAll()
                    .anyRequest().hasAnyAuthority("USER")
                .and()
                    .csrf().disable()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .logout()
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies(accessTokenName, refreshTokenName)
                    .permitAll()
                .and()
                    .addFilterBefore(new JwtFilter(jwtConfig, cookieUtils), UsernamePasswordAuthenticationFilter.class)
                    .formLogin()
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .failureHandler(failHandler)
                    .permitAll()
                .and()
                    .oauth2Login()
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .failureHandler(failHandler)
                        .userInfoEndpoint()
                        .userService(customOAuth2UserService)
        ;
        return http.build();
    }
}
