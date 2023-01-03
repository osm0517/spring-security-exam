package com.example.springsecurityexam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.springsecurityexam.repository")
public class JPAConfig {
}
