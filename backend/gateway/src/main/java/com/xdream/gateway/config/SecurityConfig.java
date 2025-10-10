package com.xdream.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf().disable()
            .authorizeExchange(exchanges -> exchanges
                // 允许访问的公开路径
                .pathMatchers(HttpMethod.GET, "/api/llm/chat/stream").permitAll()
                .pathMatchers(HttpMethod.OPTIONS, "/api/llm/chat/stream").permitAll()
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers("/eureka/**").permitAll()
                // 其他所有请求都需要认证
                .anyExchange().authenticated()
            )
            .httpBasic().disable()
            .formLogin().disable()
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        
        return http.build();
    }
}