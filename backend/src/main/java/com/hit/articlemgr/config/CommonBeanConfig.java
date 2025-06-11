package com.hit.articlemgr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 和安全框架没强耦合的通用 Bean 统一放这里，
 * 避免 SecurityConfig 与业务 Service 互相依赖、形成循环。
 */
@Configuration
public class CommonBeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
