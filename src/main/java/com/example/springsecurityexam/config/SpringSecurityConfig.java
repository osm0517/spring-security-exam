package com.example.springsecurityexam.config;

import com.example.springsecurityexam.auth.handler.FailHandlerImpl;
import com.example.springsecurityexam.auth.handler.SuccessHandlerImpl;
import com.example.springsecurityexam.auth.service.CustomOAuth2UserService;
import com.example.springsecurityexam.auth.service.UserDetailsServiceImpl;
import com.example.springsecurityexam.config.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

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
//                    .requestMatchers("/", "/", "/css/**").permitAll()
                    .anyRequest().permitAll()
//                    .anyRequest().authenticated()
                .and()
                    .csrf().disable()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .formLogin()
                    .usernameParameter("userId")
                    .passwordParameter("password")
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                .successHandler(successHandler)
//                .failureHandler(failHandler)
                    .permitAll()
                .and()
//                    .usernameParameter("userId")
//                    .passwordParameter("password")
//                    .permitAll()
//                .and()
//                    .userDetailsService(userDetailsService)
//                    .addFilterBefore(new JwtFilter(jwtConfig, cookieUtils), UsernamePasswordAuthenticationFilter.class)
//                    .logout()
//                        .logoutUrl("/logout")
//                        .deleteCookies(accessTokenName, refreshTokenName)
//                        .logoutSuccessUrl("/")
//                .and()
                    .oauth2Login()
                        .loginPage("/login")
//                    .authorizationEndpoint()
//                .authorizationRequestResolver()
//                        .baseUri("/oauth2/authorize")
//                .and()
                    .userInfoEndpoint()
                        .userService(customOAuth2UserService)
        ;
        return http.build();
    }
}
