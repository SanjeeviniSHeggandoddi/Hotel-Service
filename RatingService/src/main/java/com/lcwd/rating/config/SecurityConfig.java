package com.lcwd.rating.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurityFilter security) throws Exception{

       security
               .authorizeHttpRequest()
               .anyRequest()
               .authenticated()
               .and()
               .oauth2ResourceServer()
               .jwt();
       return security.build();

    }
}
